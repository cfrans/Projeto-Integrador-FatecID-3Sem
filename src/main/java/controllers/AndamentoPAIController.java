package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.ControleAcesso;

import java.io.IOException;

public class AndamentoPAIController {

    @FXML
    private Button btAbrirDescricao;

    @FXML
    private Button btCriarFinalizacao;

    @FXML
    private DatePicker dpRevisaoDoPlano;

    @FXML
    private TextField tfNomeAluno;

    @FXML
    private TextField tfResponsavelPlano;

    @FXML
    private TextField tfStatus;

    @FXML
    private TextField tfTituloPlano;


    @FXML
    void onClickAbrirDescricao(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/PAI.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    void onClickCriarFinalizacao(ActionEvent event) throws IOException {
        if (!ControleAcesso.verificarPermissao("T.I.","Professor","Profissional Especializado")) return;
        Parent root = FXMLLoader.load(getClass().getResource("/view/FinalizacaoPAI.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    void onClickVoltar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Menu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
