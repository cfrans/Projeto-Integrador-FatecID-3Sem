package controllers;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import org.mindrot.jbcrypt.BCrypt;

import database.ConexaoDB;
import util.FuncaoItem;

public class CadastroUsuarioController implements Initializable {

    @FXML
    private Button btCadastrar;
    @FXML
    private Button btVoltar;
    @FXML
    private TextField tfConfirmacaoSenha;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfNomeAcesso;
    @FXML
    private TextField tfSenha;
    @FXML
    private ChoiceBox<FuncaoItem> chFuncao;
    private ObservableList<FuncaoItem> listaFuncoes = FXCollections.observableArrayList();

    /**
     * Roda quando a tela é carregada
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Vincula a lista ao ChoiceBox
        chFuncao.setItems(listaFuncoes);
        // 2. Carrega os dados do banco
        carregarFuncoes();
    }

    /**
     * Carrega as funções da tabela 'funcao'
     */
    private void carregarFuncoes() {
        String sql = "SELECT id_funcao, nome FROM funcao ORDER BY nome";
        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            listaFuncoes.clear();
            while (rs.next()) {
                listaFuncoes.add(new FuncaoItem(
                        rs.getInt("id_funcao"),
                        rs.getString("nome")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar funções!");
            e.printStackTrace();
            exibirAlertaErro("Erro de Banco", "Falha ao carregar lista de funções.", e.getMessage());
        }
    }


    @FXML
    void onClickCadastrar(ActionEvent event) {

        String nome = tfNomeAcesso.getText();
        String email = tfEmail.getText();
        String senha = tfSenha.getText();
        String confirmSenha = tfConfirmacaoSenha.getText();
        FuncaoItem funcaoSel = chFuncao.getValue();

        // --- 1. Validação dos Campos ---
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || funcaoSel == null) {
            exibirAlertaErro("Campos Obrigatórios", "Nome, Email, Senha e Função são obrigatórios.", "");
            return;
        }

        if (!senha.equals(confirmSenha)) {
            exibirAlertaErro("Senhas Diferentes", "A senha e a confirmação de senha não são iguais.", "");
            return;
        }

        // --- 2. Hashing da Senha ---
        String senhaHash = BCrypt.hashpw(senha, BCrypt.gensalt());

        System.out.println("Cadastro de usuário: " + nome);
        System.out.println("Senha Hash: " + senhaHash);

        // --- 3. SQL Insert ---
        String sql = "INSERT INTO usuario (nome, email, id_funcao, senha_hash) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setInt(3, funcaoSel.getId());
            stmt.setString(4, senhaHash); // Salva o HASH, não a senha

            stmt.executeUpdate();

            System.out.println("Usuário salvo com sucesso!");

            NavegadorUtil.exibirSucessoEVOLTAR(
                    event,
                    "Sucesso",
                    "Usuário '" + nome + "' cadastrado com sucesso!"
            );

        } catch (SQLException e) {
            System.err.println("Erro ao salvar usuário:");
            e.printStackTrace();
            // Verifica se é um erro de email duplicado (unique constraint)
            if (e.getSQLState().equals("23505")) {
                exibirAlertaErro("Erro de Banco", "Email já cadastrado.", "O email '" + email + "' já existe no sistema.");
            } else {
                exibirAlertaErro("Erro de Banco", "Não foi possível salvar o usuário.", "Erro: " + e.getMessage());
            }
        }
    }

    /**
     * Helper para Alertas de Erro
     */
    private void exibirAlertaErro(String titulo, String cabecalho, String conteudo) {
        Alert alertErro = new Alert(Alert.AlertType.ERROR);
        alertErro.setTitle(titulo);
        alertErro.setHeaderText(cabecalho);
        alertErro.setContentText(conteudo);
        alertErro.showAndWait();
    }

    @FXML
    void onClickVoltar(ActionEvent event) {
        // Chama o metodo estatico da NavegadorUtil
        NavegadorUtil.voltarParaMenu(event);
    }
}