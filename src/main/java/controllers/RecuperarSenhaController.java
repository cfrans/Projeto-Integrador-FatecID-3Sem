package controllers;


import database.ConexaoDB;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RecuperarSenhaController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtNovaSenha;

    @FXML
    void onRedefinirSenha() {
        String usuario = txtUsuario.getText();
        String novaSenha = txtNovaSenha.getText();

        if (usuario.isEmpty() || novaSenha.isEmpty()) {
            mostrarAlerta("Erro", "Preencha todos os campos!", Alert.AlertType.ERROR);
            return;
        }

        try (Connection conn = ConexaoDB.getConexao()) {

            // 1. Verifica usuário pelo e-mail
            String sqlBusca = "SELECT id_usuario FROM usuario WHERE email = ?";
            PreparedStatement stmtBusca = conn.prepareStatement(sqlBusca);
            stmtBusca.setString(1, usuario);
            ResultSet rs = stmtBusca.executeQuery();

            if (!rs.next()) {
                mostrarAlerta("Erro", "Usuário não encontrado.", Alert.AlertType.ERROR);
                return;
            }

            int idUsuario = rs.getInt("id_usuario");

            // 2. Gera hash BCrypt da nova senha
            String hash = BCrypt.hashpw(novaSenha, BCrypt.gensalt(12));

            // 3. Atualiza a senha no banco
            String sqlUpdate = "UPDATE usuario SET senha_hash = ? WHERE id_usuario = ?";
            PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate);
            stmtUpdate.setString(1, hash);
            stmtUpdate.setInt(2, idUsuario);

            stmtUpdate.executeUpdate();

            mostrarAlerta("Sucesso", "Senha redefinida com sucesso!", Alert.AlertType.INFORMATION);

            fechar();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Falha ao redefinir senha.", Alert.AlertType.ERROR);
        }
    }


    @FXML
    void onCancelar() {
        fechar();
    }

    private void fechar() {
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String msg, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

