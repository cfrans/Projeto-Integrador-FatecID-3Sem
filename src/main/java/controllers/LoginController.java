package controllers;



import javafx.event.ActionEvent;

import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;

import javafx.scene.Node;

import javafx.scene.Parent;

import javafx.scene.Scene;

import javafx.scene.control.Button;

import javafx.scene.control.ChoiceBox;

import javafx.scene.control.PasswordField;

import javafx.scene.control.TextField;

import javafx.stage.Stage;



import java.io.IOException;



public class LoginController {



    @FXML

    private Button btEntrar;



    @FXML

    private Button btIrParaCadastro;



    @FXML

    private ChoiceBox<?> chPerfil;



    @FXML

    private PasswordField pfSenha;



    @FXML

    private TextField tfEmail;



    @FXML

    void onClickEntrar(ActionEvent event) {

        String email = tfEmail.getText();

        String senha = pfSenha.getText();

        System.out.println("Tentando login com: " + email + " / " + senha);



        try {

        // Chama a função mudarTela para ir para a TelaPrincipal.fxml

            mudarTela(event, "/view/Menu.fxml");

        } catch (IOException e) {

            e.printStackTrace();

            System.err.println("Erro ao carregar a tela principal: " + e.getMessage());

        }

    }









    private void mudarTela(ActionEvent event, String caminhoFXML) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource(caminhoFXML));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(new Scene(root));

        stage.show();

    }

}


