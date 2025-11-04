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
import util.CoordenadorItem;

public class PAIController implements Initializable {

    @FXML private Button btAdicionarMeta;
    @FXML private Button btLimpar;
    @FXML private Button btSalvar;
    @FXML private Button btVoltar;
    @FXML private DatePicker dpRevisaoPlano; // [cite: 116]
    @FXML private TextArea taDescricaoPlano; // [cite: 117]
    @FXML private TextField tfMeta; // [cite: 119]
    @FXML private TextField tfRA; // [cite: 117]
    @FXML private TextField tfRecursos; // [cite: 121]
    @FXML private TextField tfSerieTurma; // [cite: 122]
    @FXML private TextField tfTituloPlano; // [cite: 123]
    @FXML private ChoiceBox<AlunoItem> chNome; // [cite: 122]
    @FXML private ChoiceBox<CoordenadorItem> chResponsavelPlano; // [cite: 123]

    // Listas para guardar os dados do banco
    private ObservableList<AlunoItem> listaAlunos = FXCollections.observableArrayList();
    private ObservableList<CoordenadorItem> listaCoordenadores = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Vincular as listas aos ChoiceBoxes
        chNome.setItems(listaAlunos);
        chResponsavelPlano.setItems(listaCoordenadores);

        // 2. Carregar os dados
        carregarAlunos();
        carregarCoordenadores();

        // 3. Fazer os campos RA e Turma atualizarem sozinhos
        chNome.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> preencherDadosAluno(newVal)
        );
    }

    private void carregarAlunos() {
        String sql = "SELECT id_aluno, nome, serie_turma, ra FROM aluno ORDER BY nome";
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
        }
    }

    private void carregarCoordenadores() {
        String sql = "SELECT id_coordenador, nome FROM coordenador ORDER BY nome";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            listaCoordenadores.clear();
            while (rs.next()) {
                listaCoordenadores.add(new CoordenadorItem(rs.getInt("id_coordenador"), rs.getString("nome")));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar coordenadores!");
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

        String sql = "SELECT ra, serie_turma FROM aluno WHERE id_aluno = ?";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, aluno.getId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                tfRA.setText(String.valueOf(rs.getInt("ra"))); // [cite: 117]
                tfSerieTurma.setText(rs.getString("serie_turma")); // [cite: 122]
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void onClickSalvar(ActionEvent event) {
        // SQL 1: Inserir o PAI e pegar o ID de volta
        String sqlInsertPAI = "INSERT INTO PAI (titulo, descricao, meta, recurso_necessario, prazo_revisao, data, status, id_coordenador) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id_PAI";

        // SQL 2: Atualizar o Aluno com o ID do novo PAI
        String sqlUpdateAluno = "UPDATE aluno SET idPAI = ? WHERE id_aluno = ?";

        // Pegar os IDs dos itens selecionados nos ChoiceBoxes
        AlunoItem alunoSel = chNome.getValue();
        CoordenadorItem coordSel = chResponsavelPlano.getValue();

        // Validação
        if (alunoSel == null || coordSel == null || tfTituloPlano.getText().isEmpty()) {
            exibirAlertaErro("Campos obrigatórios", "Aluno, Responsável e Título são obrigatórios.");
            return;
        }

        Long novoPaiId = null; // Para guardar o ID do PAI

        // Inicia a Transação
        try (Connection conn = ConexaoDB.getConexao()) {

            conn.setAutoCommit(false); // <<< INICIA A TRANSAÇÃO

            // Bloco try para os statements
            try (PreparedStatement stmtInsertPAI = conn.prepareStatement(sqlInsertPAI);
                 PreparedStatement stmtUpdateAluno = conn.prepareStatement(sqlUpdateAluno)) {

                // --- ETAPA 1: Inserir o PAI ---

                System.out.println("---  1. Inserindo novo PAI ---");
                System.out.println("Título: " + tfTituloPlano.getText());
                System.out.println("ID Coordenador: " + coordSel.getId());
                System.out.println("Data Revisão: " + Date.valueOf(dpRevisaoPlano.getValue()));

                stmtInsertPAI.setString(1, tfTituloPlano.getText());
                stmtInsertPAI.setString(2, taDescricaoPlano.getText());
                stmtInsertPAI.setString(3, tfMeta.getText());
                stmtInsertPAI.setString(4, tfRecursos.getText());
                stmtInsertPAI.setDate(5, Date.valueOf(dpRevisaoPlano.getValue()));
                stmtInsertPAI.setDate(6, Date.valueOf(LocalDate.now())); // Data de hoje
                stmtInsertPAI.setString(7, "Em Andamento"); // Status inicial
                stmtInsertPAI.setInt(8, coordSel.getId()); // ID do Coordenador

                // Executa e pega o ID
                ResultSet rs = stmtInsertPAI.executeQuery();
                if (rs.next()) {
                    novoPaiId = rs.getLong(1);
                    System.out.println(" PAI salvo com ID: " + novoPaiId);
                } else {
                    throw new SQLException("Falha ao salvar PAI, ID não retornado.");
                }

                // --- ETAPA 2: Atualizar o Aluno ---
                System.out.println("---  2. Atualizando Aluno ---");
                System.out.println("ID Aluno: " + alunoSel.getId());
                System.out.println("Associando com idPAI: " + novoPaiId);

                stmtUpdateAluno.setLong(1, novoPaiId);
                stmtUpdateAluno.setInt(2, alunoSel.getId());

                int linhasAfetadas = stmtUpdateAluno.executeUpdate();
                if (linhasAfetadas == 0) {
                    throw new SQLException("Falha ao associar PAI ao Aluno (ID Aluno não encontrado: " + alunoSel.getId() + ")");
                }
                System.out.println(" Aluno atualizado com sucesso!");

                // Se deu tudo certo, COMITA a transação
                conn.commit();
                System.out.println(" Transação concluída! PAI e Aluno associados.");

                // Sucesso
                NavegadorUtil.exibirSucessoEVOLTAR(event, "Salvo com sucesso!",
                        "PAI salvo e associado ao aluno com sucesso!");

            } catch (Exception e) {
                // Se qualquer etapa falhar, desfaz TUDO
                System.err.println("Erro durante a transação, executando rollback...");
                conn.rollback(); // Desfaz as operações
                throw e; // Joga o erro para o catch externo
            }

        } catch (SQLException e) {
            exibirAlertaErro("Erro de Banco de Dados", "Erro: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            exibirAlertaErro("Erro", "Verifique se todas as datas foram preenchidas.");
            e.printStackTrace();
        }
    }

    private void exibirAlertaErro(String cabecalho, String conteudo) {
        Alert alertErro = new Alert(Alert.AlertType.ERROR);
        alertErro.setTitle("Erro");
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