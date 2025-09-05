package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {

    @FXML
    private AnchorPane anchorRoot; // 👈 usado para pegar a Stage

    @FXML
    private MenuItem menuNovoCadastro;

    @FXML
    private MenuItem menuPlano;

    @FXML
    private MenuItem menuHistorico;

    @FXML
    private MenuItem menuRendimento;

    @FXML
    private MenuItem menuAcompanhamento;

    @FXML
    private MenuItem menuSair;

    @FXML
    private MenuItem menuPerfil;

    @FXML
    private MenuItem menuDadosAluno;

    @FXML
    private MenuItem menuProjeto;

    @FXML
    void abrirNovoCadastro() throws IOException {
        mudarTela("/view/Cadastro.fxml");
    }

    @FXML
    void abrirPlano() throws IOException {
        mudarTela("/view/PAI.fxml");
    }

    @FXML
    void abrirHistorico() throws IOException {
        mudarTela("/view/Intervencoes.fxml");
    }

    @FXML
    void abrirRendimento() throws IOException {
        mudarTela("/view/Rendimento.fxml");
    }

    // --------- Método genérico para trocar de tela ----------
    private void mudarTela(String caminhoFXML) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(caminhoFXML));
        Stage stage = (Stage) anchorRoot.getScene().getWindow(); // pega a Stage a partir do AnchorPane
        stage.setScene(new Scene(root));
        stage.show();
    }
}


