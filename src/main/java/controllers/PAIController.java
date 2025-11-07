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
import util.UsuarioItem;

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
    @FXML private ChoiceBox<UsuarioItem> chResponsavelPlano;

    // Listas para guardar os dados do banco
    private ObservableList<AlunoItem> listaAlunos = FXCollections.observableArrayList();
    private ObservableList<UsuarioItem> listaUsuarios = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Vincular as listas aos ChoiceBoxes
        chNome.setItems(listaAlunos);
        chResponsavelPlano.setItems(listaUsuarios);

        // 2. Carregar os dados
        carregarAlunos();
        carregarUsuarios();

        // 3. Fazer os campos RA e Turma atualizarem sozinhos
        chNome.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> preencherDadosAluno(newVal)
        );
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
            // Adicionamos um alerta de erro para o usuário final também
            exibirAlertaErro("Erro de Banco", "Falha Crítica ao Carregar Alunos", "Não foi possível carregar a lista de alunos. " + e.getMessage());
        }
    }

    /**
     * Carrega os USUÁRIOS (ex: Coordenadores) do banco
     */
    private void carregarUsuarios() {
        String sql = "SELECT id_usuario, nome FROM usuario ORDER BY nome";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            listaUsuarios.clear();
            while (rs.next()) {
                listaUsuarios.add(new UsuarioItem(
                        rs.getInt("id_usuario"),
                        rs.getString("nome")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar usuários!");
            e.printStackTrace();
        }
    }

    // Metodo que é chamado quando um aluno é selecionado no dropdown
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

        String sqlInsertPAI = "INSERT INTO pai (titulo, descricao, meta, recurso_necessario, prazo_revisao, status, id_aluno, id_usuario) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // Pegar os IDs dos itens selecionados nos ChoiceBoxes
        AlunoItem alunoSel = chNome.getValue();
        UsuarioItem usuarioSel = chResponsavelPlano.getValue();

        // Validação
        if (alunoSel == null || usuarioSel == null || tfTituloPlano.getText().isEmpty() || dpRevisaoPlano.getValue() == null) {
            exibirAlertaErro("Erro de Validação", "Campos obrigatórios", "Aluno, Responsável, Título e Prazo de Revisão são obrigatórios.");
            return;
        }

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmtInsertPAI = conn.prepareStatement(sqlInsertPAI)) {

            // --- 1. Inserir o PAI ---
            System.out.println("---  Inserindo novo PAI ---");
            System.out.println("ID Aluno: " + alunoSel.getId());
            System.out.println("ID Usuário (Responsável): " + usuarioSel.getId());

            stmtInsertPAI.setString(1, tfTituloPlano.getText());
            stmtInsertPAI.setString(2, taDescricaoPlano.getText());
            // TODO: Você tem 3 campos de meta no FXML (tfMeta, tfMeta2, tfMeta3)
            // mas o banco só tem 1 coluna.
            // Estamos salvando apenas a Meta 1.
            stmtInsertPAI.setString(3, tfMeta.getText());
            stmtInsertPAI.setString(4, tfRecursos.getText());
            stmtInsertPAI.setDate(5, Date.valueOf(dpRevisaoPlano.getValue()));
            stmtInsertPAI.setString(6, "Em Andamento"); // Status inicial
            stmtInsertPAI.setInt(7, alunoSel.getId()); // ID do Aluno
            stmtInsertPAI.setInt(8, usuarioSel.getId()); // ID do Usuário Responsável (do ChoiceBox)

            stmtInsertPAI.executeUpdate();

            System.out.println(" PAI salvo e associado ao aluno com sucesso!");

            // Sucesso
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
        alertErro.setTitle(titulo); //
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

        //Limpando ChoiceBoxes (Dropdowns)
        chNome.getSelectionModel().clearSelection();
        chResponsavelPlano.getSelectionModel().clearSelection();

        //Limpando o DatePicker
        dpRevisaoPlano.setValue(null);

        //Limpando o TextArea
        taDescricaoPlano.clear();

        //Coloca o foco do cursor de volta no primeiro campo
        chNome.requestFocus();
    }

    @FXML void onClickVoltar(ActionEvent event) {
        System.out.println("Clicado em voltar.\nChamando o método estático de voltar ao menu");
        // Chama o metodo estatico da NavegadorUtil
        NavegadorUtil.voltarParaMenu(event);
    }
}