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

    /**
     * Verifica se uma senha atende aos requisitos de força usando apenas uma expressão regular.
     *
     * Regras exigidas:
     * - Pelo menos 8 caracteres
     * - Pelo menos 1 letra maiúscula
     * - Pelo menos 1 letra minúscula
     * - Pelo menos 1 número
     * - Pelo menos 1 caractere especial
     */
    private boolean senhaAtendeRequisitos(String senha) {
        return senha.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$");
    }

    /**
     * Evento acionado ao clicar em "Redefinir Senha".
     */
    @FXML
    void onRedefinirSenha() {
        String usuario = txtUsuario.getText();
        String novaSenha = txtNovaSenha.getText();

        if (usuario.isEmpty() || novaSenha.isEmpty()) {
            mostrarAlerta("Erro", "Preencha todos os campos!", Alert.AlertType.ERROR);
            return;
        }

        // 🔥 Verificação da senha — apenas 1 condição
        if (!senhaAtendeRequisitos(novaSenha)) {
            mostrarAlerta(
                    "Senha inválida",
                    "A senha deve conter obrigatoriamente:\n" +
                            "• Pelo menos 8 caracteres\n" +
                            "• Pelo menos 1 letra maiúscula (A–Z)\n" +
                            "• Pelo menos 1 letra minúscula (a–z)\n" +
                            "• Pelo menos 1 número (0–9)\n" +
                            "• Pelo menos 1 caractere especial (!@#$% etc.)",
                    Alert.AlertType.WARNING
            );
            return;
        }

        try (Connection conn = ConexaoDB.getConexao()) {

            // 1. Busca usuário pelo email
            String sqlBusca = "SELECT id_usuario FROM usuario WHERE email = ?";
            PreparedStatement stmtBusca = conn.prepareStatement(sqlBusca);
            stmtBusca.setString(1, usuario);
            ResultSet rs = stmtBusca.executeQuery();

            if (!rs.next()) {
                mostrarAlerta("Erro", "Usuário não encontrado.", Alert.AlertType.ERROR);
                return;
            }

            int idUsuario = rs.getInt("id_usuario");

            // 2. Gera hash da nova senha
            String hash = BCrypt.hashpw(novaSenha, BCrypt.gensalt(12));

            // 3. Atualiza senha no banco
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

    /**
     * Botão Cancelar → fecha a tela.
     */
    @FXML
    void onCancelar() {
        fechar();
    }

    /**
     * Fecha a janela atual.
     */
    private void fechar() {
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        stage.close();
    }

    /**
     * Exibe um alerta simples.
     */
    private void mostrarAlerta(String titulo, String msg, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}


