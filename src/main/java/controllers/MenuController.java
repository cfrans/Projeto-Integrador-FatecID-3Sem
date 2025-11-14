package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.ControladorNavegavel;
import util.ControleAcesso;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML
    private AnchorPane anchorRoot;
    @FXML
    private MenuItem menuNovoCadastro;
    @FXML
    private MenuItem menuPlano;
    @FXML
    private MenuItem menuCadastrarUsuario;
    @FXML
    private MenuItem menuHistorico;
    @FXML
    private MenuItem menuRendimento;
    @FXML
    private MenuItem menuAcompanhamento;
    @FXML
    private MenuItem menuSair;
    @FXML
    private MenuItem menuTrocarUsuario;
    @FXML
    private MenuItem menuProjeto;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Carrega a tela "Home" como tela inicial
        navegarPara("/view/Home.fxml");
    }

    @FXML
    void abrirCadastrarUsuario() throws IOException {
        if (!ControleAcesso.verificarPermissao("T.I.", "Coordenador")) return;
        mudarTela("/view/CadastroUsuario.fxml");
    }

    @FXML
    void abrirNovoCadastro() throws IOException {
        if (!ControleAcesso.verificarPermissao("T.I.","Coordenador")) return;
        mudarTela("/view/Cadastro.fxml");
    }

    @FXML
    void abrirPlano() throws IOException {
        if (!ControleAcesso.verificarPermissao("T.I.","Professor","Profissional Especializado")) return;
        mudarTela("/view/PAI.fxml");
    }

    @FXML
    void abrirHistorico() throws IOException {
        if (!ControleAcesso.verificarPermissao("T.I.","Professor","Coordenador","Profissional Especializado")) return;
        mudarTela("/view/Intervencoes.fxml");
    }

    @FXML
    void abrirRendimento() throws IOException {
        if (!ControleAcesso.verificarPermissao("T.I.","Professor")) return;
        mudarTela("/view/Rendimento.fxml");
    }

    @FXML
    void abrirAndamento() throws IOException {
        if (!ControleAcesso.verificarPermissao("T.I.","Professor","Profissional Especializado")) return;
        mudarTela("/view/AndamentoPAI.fxml");
    }

    @FXML
    void abrirConsultaRendimento() throws IOException {
        if (!ControleAcesso.verificarPermissao("T.I.","Professor","Profissional Especializado", "Coordenador")) return;
        mudarTela("/view/ConsultaRendimento.fxml");
    }

    // --- Métodos de 'Sair' e 'Trocar Usuário' ---
    @FXML
    void trocarUsuario() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        Stage stage = (Stage) anchorRoot.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void sairSistema() throws IOException {
        NavegadorUtil.fecharAplicacao();
    }

    private void mudarTela(String caminhoFXML) throws IOException {
        try {
            URL fxmlUrl = getClass().getResource(caminhoFXML);
            if (fxmlUrl == null) {
                System.err.println("Erro: Não foi possível encontrar o FXML: " + caminhoFXML);
                return;
            }

            // 1. Cria um FXMLLoader em vez de carregar direto
            FXMLLoader loader = new FXMLLoader(fxmlUrl);

            // 2. Carrega o AnchorPane
            AnchorPane paginaCarregada = loader.load();

            // 3. LIMPA E ADICIONA A PÁGINA
            anchorRoot.getChildren().clear();
            anchorRoot.getChildren().setAll(paginaCarregada);
            AnchorPane.setTopAnchor(paginaCarregada, 0.0);
            AnchorPane.setBottomAnchor(paginaCarregada, 0.0);
            AnchorPane.setLeftAnchor(paginaCarregada, 0.0);
            AnchorPane.setRightAnchor(paginaCarregada, 0.0);

            // 4. ENTREGAR A SI MESMO PARA O NOVO CONTROLADOR
            Object controller = loader.getController();
            if (controller instanceof ControladorNavegavel) {
                ((ControladorNavegavel) controller).setMenuController(this);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void navegarPara(String caminhoFXML) {
        try {
            mudarTela(caminhoFXML);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: adicionar talvez um Alert
        }
    }
}