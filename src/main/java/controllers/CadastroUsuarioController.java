package controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CadastroUsuarioController {

    @FXML
    private Button btCadastrar;

    @FXML
    private Button btVoltar;

    @FXML
    private ChoiceBox<?> chFuncao;

    @FXML
    private TextField tfConfirmacaoSenha;

    @FXML
    private TextField tfEmail;

    @FXML
    private TextField tfNomeAcesso;

    @FXML
    private TextField tfSenha;

    @FXML
    void onClickCadastrar(ActionEvent event) {
        // lógica de cadastro
        System.out.println("Cadastro feito para: " + tfNomeAcesso.getText());

        try {
            // depois de cadastrar, vai para a tela de Menu
            mudarTela(event, "/view/Menu.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar a tela de Menu: " + e.getMessage());
        }
    }

    @FXML
    void onClickVoltar(ActionEvent event) {
        try {
            mudarTela(event, "/view/Login.fxml"); // volta para a tela de login
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mudarTela(ActionEvent event, String caminhoFXML) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(caminhoFXML));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
