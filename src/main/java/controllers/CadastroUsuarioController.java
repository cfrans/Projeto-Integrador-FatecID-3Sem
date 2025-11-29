package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

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
import util.SessaoUsuario;
import controllers.NavegadorUtil;

public class CadastroUsuarioController extends BaseController implements Initializable {

    @FXML private Button btCadastrar;
    @FXML private Button btVoltar;
    @FXML private TextField tfConfirmacaoSenha;
    @FXML private TextField tfEmail;
    @FXML private TextField tfNomeAcesso;
    @FXML private TextField tfSenha;
    @FXML private ChoiceBox<FuncaoItem> chFuncao;

    private final ObservableList<FuncaoItem> listaFuncoes = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // --- Bloqueia tipos não permitidos nos campos digitáveis e ajusta tamanhos ---
        campoSomenteTexto(tfNomeAcesso);
        limitarTamanhoCampo(tfNomeAcesso, 100);
        limitarTamanhoCampo(tfEmail, 100);

        chFuncao.setItems(listaFuncoes);
        carregarFuncoes();

        // Se for o admin padrão
        if (SessaoUsuario.isModoAdminPadrao()) {
            btVoltar.setDisable(true); // Impede que ele volte para o Home sem criar o usuário
        }
    }

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
            exibirAlertaErro("Erro de Banco", "Falha ao carregar lista de funções.", e.getMessage());
        }
    }

    /** Validação de senha forte */
    private boolean senhaForte(String senha) {
        // Mínimo 8 chars, 1 Maiúscula, 1 minúscula, 1 número, 1 especial
        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!?.*()\\-_]).{8,}$";
        return senha.matches(regex);
    }

    @FXML
    void onClickCadastrar(ActionEvent event) {

        String nome = tfNomeAcesso.getText();
        String email = tfEmail.getText();
        String senha = tfSenha.getText();
        String confirmSenha = tfConfirmacaoSenha.getText();
        FuncaoItem funcaoSel = chFuncao.getValue();

        // --- 1. Validações Básicas ---
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || funcaoSel == null) {
            exibirAlertaErro("Campos Obrigatórios", "Nome, Email, Senha e Função são obrigatórios.", "");
            return;
        }

        // --- Email válido ---
        if (!validacaoEmail(email)) {
            exibirAlertaErro("Email inválido", "O email informado não está em um formato válido.",
                    "Exemplos: usuario@gmail.com, teste@yahoo.com");
            return;
        }

        // --- Senha forte ---
        if (!senhaForte(senha)) {
            exibirAlertaErro("Senha Fraca", "A senha deve conter no mínimo 8 caracteres e incluir:",
                    "- Uma letra maiúscula\n- Uma letra minúscula\n- Um número\n- Um caractere especial");
            return;
        }

        // --- Confirmação de senha ---
        if (!senha.equals(confirmSenha)) {
            exibirAlertaErro("Senhas Diferentes", "A senha e a confirmação de senha não são iguais.", "");
            return;
        }

        // --- 2. Processo de Gravação ---
        String senhaHash = BCrypt.hashpw(senha, BCrypt.gensalt());
        String sql = "INSERT INTO usuario (nome, email, id_funcao, senha_hash) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setInt(3, funcaoSel.getId());
            stmt.setString(4, senhaHash);

            stmt.executeUpdate(); // Usuário novo criado com sucesso!

            // ==============================================================================
            // LÓGICA DO ADMIN PADRÃO (Troca Forçada)
            // ==============================================================================
            if (SessaoUsuario.isModoAdminPadrao()) {

                // 1. Remove o usuário 'admin' antigo do banco
                deletarUsuarioAdminPadrao();

                // 2. Avisa o usuário
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Configuração Concluída");
                alert.setHeaderText("Novo administrador configurado com sucesso!");
                alert.setContentText("O usuário padrão 'admin' foi removido por segurança.\n" +
                        "O sistema será encerrado agora. Por favor, faça login com seu NOVO usuário.");
                alert.showAndWait(); // Espera o usuário dar OK

                // 3. Força o Logoff (Volta para Login)
                if (menuController != null) {
                    try {
                        menuController.trocarUsuario();
                    } catch (IOException e) {
                        e.printStackTrace();
                        exibirAlertaErro("Erro", "Erro ao redirecionar para login.", e.getMessage());
                    }
                }
                return; // Interrompe o método aqui para não executar o fluxo normal abaixo
            }
            // ==============================================================================

            // Fluxo Normal (não é admin padrão)

            NavegadorUtil.exibirSucessoAlerta(
                    "Sucesso",
                    "Usuário '" + nome + "' cadastrado com sucesso!",
                    menuController.getStage()
            );
            navegarParaHome();

        } catch (SQLException e) {
            // Tratamento de erro de banco (Duplicidade, etc)
            if ("23505".equals(e.getSQLState())) {
                exibirAlertaErro("Erro de Banco", "Email já cadastrado.",
                        "O email '" + email + "' já existe no sistema.");
            } else {
                exibirAlertaErro("Erro de Banco", "Não foi possível salvar o usuário.", e.getMessage());
            }
        }
    }

    /**
     * Remove o usuário temporário 'admin' do banco de dados.
     */
    private void deletarUsuarioAdminPadrao() {
        String sql = "DELETE FROM usuario WHERE email = 'admin'"; //

        try (Connection conn = ConexaoDB.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Segurança: Usuário 'admin' padrão removido com sucesso.");
            } else {
                System.err.println("Atenção: Não foi possível remover o usuário 'admin'. Verifique se o email está correto.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erro crítico ao tentar deletar admin padrão: " + e.getMessage());
            // Opcional: Exibir erro para o usuário, mas geralmente queremos que ele prossiga para o login
        }
    }

    private void limparCampos() {
        tfNomeAcesso.clear();
        tfEmail.clear();
        tfSenha.clear();
        tfConfirmacaoSenha.clear();
        chFuncao.setValue(null);
    }
}