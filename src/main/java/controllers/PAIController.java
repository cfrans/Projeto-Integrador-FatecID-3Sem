package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import java.time.LocalDate;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import database.ConexaoDB;
import util.AlunoItem;
import util.SessaoUsuario;

public class PAIController implements Initializable {

    @FXML private TextField tfMeta2;
    @FXML private TextField tfMeta3;
    @FXML private Button btLimpar;
    @FXML private Button btSalvar;
    @FXML private Button btVoltar;
    @FXML private DatePicker dpRevisaoPlano;
    @FXML private TextArea taDescricaoPlano;
    @FXML private TextField tfMeta;
    @FXML private TextField tfRA;
    @FXML private TextField tfRecursos;
    @FXML private TextField tfSerieTurma;
    @FXML private TextField tfTituloPlano;
    @FXML private ChoiceBox<AlunoItem> chNome;
    @FXML private TextField tfResponsavelPlano;

    // Lista para guardar os dados do banco
    private ObservableList<AlunoItem> listaAlunos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Vincular lista de Alunos
        chNome.setItems(listaAlunos);

        // 2. Carregar Alunos
        carregarAlunos();

        // 3. Fazer os campos RA e Turma atualizarem sozinhos
        chNome.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> preencherDadosAluno(newVal)
        );

        // 4. Preenche o responsável com o usuário logado e desabilita o campo
        if (SessaoUsuario.getNomeUsuario() != null) {
            tfResponsavelPlano.setText(SessaoUsuario.getNomeUsuario());
            tfResponsavelPlano.setDisable(true); // Deixa o campo cinza/não editável
        } else {
            // Caso ninguém esteja logado (segurança)
            tfResponsavelPlano.setText("Nenhum usuário logado");
            tfResponsavelPlano.setDisable(true);
        }
    }

    private void carregarAlunos() {
        String sql = "SELECT id_aluno, nome FROM aluno ORDER BY nome";

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            listaAlunos.clear();
            while (rs.next()) {
                listaAlunos.add(new AlunoItem(rs.getInt("id_aluno"), rs.getString("nome")));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar alunos!");
            e.printStackTrace();
            exibirAlertaErro("Erro de Banco", "Falha Crítica ao Carregar Alunos", "Não foi possível carregar a lista de alunos. " + e.getMessage());
        }
    }


    private void preencherDadosAluno(AlunoItem aluno) {
        if (aluno == null) {
            tfRA.clear();
            tfSerieTurma.clear();
            return;
        }

        String sql = "SELECT a.ra, s.nome AS nome_serie FROM aluno a " +
                "JOIN serie_turma s ON a.id_serie_turma = s.id_serie_turma " +
                "WHERE a.id_aluno = ?";

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, aluno.getId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                tfRA.setText(rs.getString("ra"));
                tfSerieTurma.setText(rs.getString("nome_serie"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void onClickSalvar(ActionEvent event) {

        String sqlInsertPAI = "INSERT INTO pai (titulo, descricao, meta, meta2, meta3, recurso_necessario, prazo_revisao, status, id_aluno, id_usuario) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Pegar os IDs dos itens selecionados
        AlunoItem alunoSel = chNome.getValue();
        // Pega o ID do usuário logado na SESSÃO
        int idUsuarioLogado = SessaoUsuario.getIdUsuario();

        // Validação
        if (alunoSel == null || tfTituloPlano.getText().isEmpty() || dpRevisaoPlano.getValue() == null) {
            exibirAlertaErro("Erro de Validação", "Campos obrigatórios", "Aluno, Título e Prazo de Revisão são obrigatórios.");
            return;
        }

        if (idUsuarioLogado == 0) {
            exibirAlertaErro("Erro de Sessão", "Usuário não logado", "Não foi possível identificar o usuário logado. Faça login novamente.");
            return;
        }

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmtInsertPAI = conn.prepareStatement(sqlInsertPAI)) {

            System.out.println("---  Inserindo novo PAI ---");
            System.out.println("ID Aluno: " + alunoSel.getId());
            System.out.println("ID Usuário (Responsável): " + idUsuarioLogado);

            // Ordem dos parâmetros

            // 1. titulo
            stmtInsertPAI.setString(1, tfTituloPlano.getText());
            // 2. descricao
            stmtInsertPAI.setString(2, taDescricaoPlano.getText());
            // 3. meta
            stmtInsertPAI.setString(3, tfMeta.getText());
            // 4. meta2 (NOVO)
            stmtInsertPAI.setString(4, tfMeta2.getText());
            // 5. meta3 (NOVO)
            stmtInsertPAI.setString(5, tfMeta3.getText());
            // 6. recurso_necessario
            stmtInsertPAI.setString(6, tfRecursos.getText());
            // 7. prazo_revisao
            stmtInsertPAI.setDate(7, Date.valueOf(dpRevisaoPlano.getValue()));
            // 8. status
            stmtInsertPAI.setString(8, "Em Andamento"); // Status inicial
            // 9. id_aluno
            stmtInsertPAI.setInt(9, alunoSel.getId());
            // 10. id_usuario (da SESSÃO)
            stmtInsertPAI.setInt(10, idUsuarioLogado);

            stmtInsertPAI.executeUpdate();

            System.out.println(" PAI salvo e associado ao aluno com sucesso!");

            NavegadorUtil.exibirSucessoEVOLTAR(event, "Salvo com sucesso!",
                    "PAI salvo e associado ao aluno com sucesso!");

        } catch (SQLException e) {
            exibirAlertaErro("Erro de Banco de Dados", "Não foi possível salvar o PAI.", "Erro: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            exibirAlertaErro("Erro Inesperado", "Ocorreu um erro.", "Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void exibirAlertaErro(String titulo, String cabecalho, String conteudo) {
        Alert alertErro = new Alert(Alert.AlertType.ERROR);
        alertErro.setTitle(titulo);
        alertErro.setHeaderText(cabecalho);
        alertErro.setContentText(conteudo);
        alertErro.showAndWait();
    }

    @FXML void onClickAdicionarMeta(ActionEvent event) {}

    @FXML void onClickLimpar(ActionEvent event) {
        System.out.println("Limpando formulário PAI...");

        //Limpando TextFields
        tfRA.clear();
        tfMeta.clear();
        tfMeta2.clear();
        tfMeta3.clear();
        tfRecursos.clear();
        tfSerieTurma.clear();
        tfTituloPlano.clear();
        // O tfResponsavelPlano não é limpo, pois ele é estático (usuário logado)

        //Limpando ChoiceBoxes (Dropdowns)
        chNome.getSelectionModel().clearSelection();

        //Limpando o DatePicker
        dpRevisaoPlano.setValue(null);

        //Limpando o TextArea
        taDescricaoPlano.clear();

        //Coloca o foco do cursor de volta no primeiro campo
        chNome.requestFocus();
    }

    @FXML void onClickVoltar(ActionEvent event) {
        System.out.println("Clicado em voltar.\nChamando o método estático de voltar ao menu");
        NavegadorUtil.voltarParaMenu(event);
    }
}