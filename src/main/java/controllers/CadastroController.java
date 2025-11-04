package controllers;

import java.sql.ResultSet;

import javafx.collections.FXCollections; // Import para popular ChoiceBox
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.net.URL; // Para o Initializable
import java.util.ResourceBundle; // Para o Initializable
import java.util.stream.Collectors; // Import para juntar os tipos
import java.util.List; // Import para a lista de checkboxes
import java.util.Arrays; // Import para a lista de checkboxes

//Imports do banco
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;

import database.ConexaoDB;

public class CadastroController implements Initializable {

    @FXML private Button btAdicionar;
    @FXML private Button btAdicionarResponsavel;
    @FXML private Button btLimpar;
    @FXML private Button btSalvar;
    @FXML private Button btVoltar;
    @FXML private CheckBox cbTipoNecessidade1;
    @FXML private CheckBox cbTipoNecessidade2;
    @FXML private CheckBox cbTipoNecessidade3;
    @FXML private CheckBox cbTipoNecessidade4;
    @FXML private CheckBox cbTipoNecessidade5;
    @FXML private Label lbParentesco;
    @FXML private ChoiceBox<String> chParentesco; // [cite: 32]
    @FXML private ChoiceBox<String> chSerieTurma; // [cite: 28] (Mudei de <?> para <String>)
    @FXML private DatePicker dpDataLaudo;
    @FXML private DatePicker dpDataNascimento;
    @FXML private TextArea taObservacoes; // [cite: 30]
    @FXML private TextField tdNomeResponsavel; // [cite: 31]
    @FXML private TextField tfEmail; // [cite: 33]
    @FXML private TextField tfNome; // [cite: 27]
    @FXML private TextField tfNumeroLaudo; // [cite: 31]
    @FXML private TextField tfProfissional; // [cite: 34]
    @FXML private TextField tfTelefone; // [cite: 33]
    @FXML private TextField tfRA; // [cite: 35]
    @FXML private Font x1;
    @FXML private Color x2;

    // Lista de Checkboxes para o helper
    private List<CheckBox> listaNecessidades;

    /**
     * 2. Este metodo roda quando o FXML é carregado, para popular os dropdowns.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Popula o ChoiceBox de Parentesco [cite: 32]
        chParentesco.setItems(FXCollections.observableArrayList(
                "Mãe", "Pai", "Avó", "Avô", "Tio(a)", "Irmão/Irmã", "Outro"
        ));

        // Popula o ChoiceBox de Série/Turma [cite: 28]
        chSerieTurma.setItems(FXCollections.observableArrayList(
                "1º Ano A", "1º Ano B", "2º Ano A", "3º Ano A", "4º Ano A", "5º Ano A",
                "6º Ano A", "7º Ano A", "8º Ano A", "9º Ano A",
                "1º Ens. Médio", "2º Ens. Médio", "3º Ens. Médio"
        ));

        // Agrupa os checkboxes para facilitar a leitura
        listaNecessidades = Arrays.asList(cbTipoNecessidade1, cbTipoNecessidade2, cbTipoNecessidade3, cbTipoNecessidade4, cbTipoNecessidade5);
    }


    @FXML
    void onClickSalvar(ActionEvent event) {
        System.out.println("Salvar clicado. Iniciando transação...");

        // IDs para recuperar do banco
        Long idResponsavel = null;
        Long idProfissional = null;
        Long idAluno = null;

        // SQLs para cada tabela
        String sqlResponsavel = "INSERT INTO responsavel (nome, parentesco, email, telefone) VALUES (?, ?, ?, ?) RETURNING id_responsavel";

        // Busca o ID do profissional que já foi cadastrado (via simulação)
        String sqlSelectProfissional = "SELECT id_profissional_especializado FROM profissional_especializado WHERE nome = ?";

        String sqlAluno = "INSERT INTO aluno (nome, data_nascimento, serie_turma, RA, id_responsavel, id_professor, idPAI, idcoordenador) VALUES (?, ?, ?, ?, ?, NULL, NULL, NULL) RETURNING id_aluno";

        String sqlLaudo = "INSERT INTO laudo (numero, data, descricao, tipo, id_aluno, id_profissional_especializado) VALUES (?, ?, ?, ?, ?, ?)";


        try (Connection conn = ConexaoDB.getConexao()) {

            // Inicia a Transação
            conn.setAutoCommit(false);

            try (PreparedStatement stmtResp = conn.prepareStatement(sqlResponsavel);
                 PreparedStatement stmtSelectProf = conn.prepareStatement(sqlSelectProfissional);
                 PreparedStatement stmtAluno = conn.prepareStatement(sqlAluno);
                 PreparedStatement stmtLaudo = conn.prepareStatement(sqlLaudo)) {

                // --- ETAPA 1: Salvar Responsável ---
                stmtResp.setString(1, tdNomeResponsavel.getText()); // [cite: 31]
                stmtResp.setString(2, chParentesco.getValue()); // [cite: 32]
                stmtResp.setString(3, tfEmail.getText()); // [cite: 33]
                stmtResp.setString(4, tfTelefone.getText()); // [cite: 33]

                ResultSet rsResp = stmtResp.executeQuery();
                if (rsResp.next()) {
                    idResponsavel = rsResp.getLong(1);
                    System.out.println("Responsável salvo com ID: " + idResponsavel);
                } else {
                    throw new SQLException("Falha ao salvar responsável, ID não retornado.");
                }

                // --- ETAPA 2: Buscar Profissional ---
                // (Assumindo que o nome digitado em tfProfissional [cite: 34] existe no banco)
                stmtSelectProf.setString(1, tfProfissional.getText());
                ResultSet rsProf = stmtSelectProf.executeQuery();
                if (rsProf.next()) {
                    idProfissional = rsProf.getLong(1);
                    System.out.println("Profissional encontrado com ID: " + idProfissional);
                } else {
                    // Se não encontrar, o nome foi digitado errado ou não existe
                    throw new SQLException("Profissional especializado '" + tfProfissional.getText() + "' não encontrado no banco. Cadastre-o primeiro.");
                }
                // TODO: VERIFICAR SE NAO CONSEGUIMOS CADASTRAR AUTOMATICAMENTE CASO NAO EXISTA (acima)
                // TODO: OU ENTAO DEIXAR O PROFISSIONAL COMO DROPDOWN E CRIAR UM CRUD PRA ELES

                // --- ETAPA 3: Salvar Aluno ---
                stmtAluno.setString(1, tfNome.getText()); // [cite: 27]
                stmtAluno.setDate(2, Date.valueOf(dpDataNascimento.getValue()));
                stmtAluno.setString(3, chSerieTurma.getValue()); // [cite: 28]
                stmtAluno.setInt(4, Integer.parseInt(tfRA.getText())); // [cite: 35]
                stmtAluno.setLong(5, idResponsavel); // Usa o ID do Responsável

                ResultSet rsAluno = stmtAluno.executeQuery();
                if (rsAluno.next()) {
                    idAluno = rsAluno.getLong(1);
                    System.out.println("Aluno salvo com ID: " + idAluno);
                } else {
                    throw new SQLException("Falha ao salvar aluno, ID não retornado.");
                }

                // --- ETAPA 4: Salvar Laudo ---
                stmtLaudo.setInt(1, Integer.parseInt(tfNumeroLaudo.getText())); // [cite: 31]
                stmtLaudo.setDate(2, (dpDataLaudo.getValue() != null) ? Date.valueOf(dpDataLaudo.getValue()) : null);
                stmtLaudo.setString(3, taObservacoes.getText()); // [cite: 30]
                stmtLaudo.setString(4, getTipoNecessidadeSelecionada()); // Helper para pegar os checkboxes [cite: 29, 30]
                stmtLaudo.setLong(5, idAluno); // Usa o ID do Aluno
                stmtLaudo.setLong(6, idProfissional); // Usa o ID do Profissional

                stmtLaudo.executeUpdate();
                System.out.println("Laudo salvo com sucesso.");

                // Se tudo deu certo, confirma a transação
                conn.commit();

                System.out.println("Transação concluída com sucesso!");

                NavegadorUtil.exibirSucessoEVOLTAR(
                        event,
                        "Cadastro",
                        "Aluno, Responsável e Laudo salvos com sucesso!"
                );

            } catch (Exception e) {
                System.err.println("Erro durante a transação, executando rollback...");
                conn.rollback(); // DESFAZ TUDO
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Erro de SQL ao salvar no banco de dados:");
            e.printStackTrace();
            exibirAlertaErro("Erro de Banco de Dados", "Não foi possível salvar o registro.", "Erro: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Erro de formato de número (RA ou Nro Laudo):");
            e.printStackTrace();
            exibirAlertaErro("Erro de Formato", "RA e Número do Laudo devem ser números.", "Erro: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado:");
            e.printStackTrace();
            exibirAlertaErro("Erro Inesperado", "Ocorreu um erro.", "Erro: " + e.getMessage());
        }
    }


    // --- MÉTODOS HELPER (AJUDANTES) ---

    /**
     * Helper para exibir um Alerta de Erro padronizado.
     */
    private void exibirAlertaErro(String titulo, String cabecalho, String conteudo) {
        Alert alertErro = new Alert(Alert.AlertType.ERROR);
        alertErro.setTitle(titulo);
        alertErro.setHeaderText(cabecalho);
        alertErro.setContentText(conteudo);
        alertErro.showAndWait();
    }

    /**
     * Helper para juntar os textos dos CheckBoxes [cite: 29, 30] em uma única String.
     */
    private String getTipoNecessidadeSelecionada() {
        return listaNecessidades.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.joining(", ")); // Ex: "TDAH, Dislexia"
    }

    @FXML
    void onClickAdicionar(ActionEvent event) {
        System.out.println("Adicionar clicado");
    }

    @FXML
    void onClickLimpar(ActionEvent event) {
        System.out.println("Limpando formulário de cadastro...");
        tfNome.clear();
        tfNumeroLaudo.clear();
        tfProfissional.clear();
        tdNomeResponsavel.clear();
        tfTelefone.clear();
        tfEmail.clear();
        tfRA.clear();
        dpDataNascimento.setValue(null);
        dpDataLaudo.setValue(null);
        chSerieTurma.getSelectionModel().clearSelection();
        chParentesco.getSelectionModel().clearSelection();
        listaNecessidades.forEach(cb -> cb.setSelected(false));
        taObservacoes.clear();
        tfNome.requestFocus();
    }

    @FXML
    void onClickVoltar(ActionEvent event) {
        System.out.println("Clicado em voltar.\nChamando o método estático de voltar ao menu.");
        NavegadorUtil.voltarParaMenu(event);
    }

    @FXML
    void onClickAdicionarResponsavel(ActionEvent event) {
        System.out.println("Adicionar Responsável clicado");
    }

    private void mudarTela(ActionEvent event, String caminhoFXML) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(caminhoFXML));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}