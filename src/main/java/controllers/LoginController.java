package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;
import database.ConexaoDB;
import util.SessaoUsuario;

public class LoginController {

    @FXML private Button btEntrar;
    @FXML private PasswordField pfSenha;
    @FXML private TextField tfEmail;

    /**
     * Manipula o evento do botão Entrar, realizando o processo de autenticação
     * do usuário no sistema
     *
     * @param event o evento disparado pelo clique no botão Entrar.
     */
    @FXML
    void onClickEntrar(ActionEvent event) {

        String email = tfEmail.getText();
        String senhaDigitada = pfSenha.getText();

        // 1. Validação de campos vazios
        if (email.isEmpty() || senhaDigitada.isEmpty()) {
            exibirAlertaErro("Campos vazios", "Email e Senha são obrigatórios.");
            return;
        }

        // 2. SQL para buscar o usuário pelo email
        //String sql = "SELECT id_usuario, nome, senha_hash FROM usuario WHERE email = ?";
        String sql = """
          SELECT u.id_usuario, u.nome, u.senha_hash, f.nome AS funcao
          FROM usuario u
          INNER JOIN funcao f ON u.id_funcao = f.id_funcao
          WHERE u.email = ?
        """;


        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            // 3. Verifica se o email foi encontrado
            if (rs.next()) {
                // Email encontrado, agora checa a senha
                String hashDoBanco = rs.getString("senha_hash");

                // 4. USA O BCRYPT para comparar a senha digitada com o hash do banco
                if (BCrypt.checkpw(senhaDigitada, hashDoBanco)) {

                    // --- SENHA CORRETA ---

                    int id = rs.getInt("id_usuario");
                    String nome = rs.getString("nome");
                    String funcao = rs.getString("funcao");

                    // 5. Salva o usuário na Sessão Global
                    SessaoUsuario.setUsuarioLogado(id, nome, funcao);
                    System.out.println("LOGIN SUCESSO! ID: " + id + ", Nome: " + nome);

                    // 6. Muda para a tela de Menu
                    mudarTela(event, "/view/Menu.fxml");


                } else {
                    // Senha incorreta
                    System.err.println("Tentativa de login: Senha incorreta para " + email);
                    exibirAlertaErro("Login Falhou", "Email ou senha inválidos.");
                }

            } else {
                // Email não encontrado
                System.err.println("Tentativa de login: Email não encontrado " + email);
                exibirAlertaErro("Login Falhou", "Email ou senha inválidos.");
            }

        } catch (SQLException e) {
            System.err.println("Erro de SQL durante o login:");
            e.printStackTrace();
            exibirAlertaErro("Erro de Banco", "Ocorreu um erro ao tentar conectar com o banco de dados.");
        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela principal:");
            e.printStackTrace();
            exibirAlertaErro("Erro de FXML", "Não foi possível carregar a tela do menu.");
        }
    }


    /**
     * Helper para Alertas de Erro
     * @param titulo
     * @param cabecalho
     */
    private void exibirAlertaErro(String titulo, String cabecalho) {
        Alert alertErro = new Alert(Alert.AlertType.ERROR);
        alertErro.setTitle(titulo);
        alertErro.setHeaderText(cabecalho);
        alertErro.setContentText(null);
        alertErro.showAndWait();
    }


    /**
     * Altera a cena atual para outra interface FXML especificada
     *
     * Este método realiza a troca de tela a partir de um evento de ação
     * Ele carrega o arquivo FXML informado,
     * obtém a janela atual a partir do componente que disparou o evento e substitui a cena exibida pela nova interface
     *
     * @param event        o evento que disparou a mudança de tela, geralmente o clique de um botão.
     * @param caminhoFXML  o caminho do arquivo FXML que será carregado para exibição.
     * @throws IOException se ocorrer qualquer erro ao tentar carregar o arquivo FXML.
     */
    private void mudarTela(ActionEvent event, String caminhoFXML) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(caminhoFXML));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}