package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import util.ControleAcesso;

import java.io.IOException;

public class MenuController {

    @FXML
    private AnchorPane anchorRoot; // 👈 usado para pegar a Stage

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


    @FXML
    void abrirCadastrarUsuario() throws IOException {
        // Verifica a permissão antes de tentar abrir a tela
        if (!ControleAcesso.verificarPermissao("T.I.", "Coordenador")) return;

        // Chama o método mudarTela com apenas o caminho FXML
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

    @FXML
    void trocarUsuario() throws IOException {
        mudarTela("/view/Login.fxml");
    }

    @FXML
    void sairSistema() throws IOException {
        // Metodo temporario para encerrar a aplicação.
        NavegadorUtil.fecharAplicacao();
    }

    // --------- Método genérico para trocar de tela ----------
    private void mudarTela(String caminhoFXML) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(caminhoFXML));
        Stage stage = (Stage) anchorRoot.getScene().getWindow(); // pega a Stage a partir do AnchorPane
        stage.setScene(new Scene(root));
        stage.show();
    }


    

}


