package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class FinalizacaoPAIController {


    @FXML
    private Button btImprimir;

    @FXML
    private Button btLmpar;

    @FXML
    private Button btSalvar;

    @FXML
    private Button btSalvarCriar;

    @FXML
    private Button btVoltar;

    @FXML
    private CheckBox cbMeta1;

    @FXML
    private CheckBox cbMeta2;

    @FXML
    private CheckBox cbMeta3;

    @FXML
    private CheckBox cbMeta4;

    @FXML
    private CheckBox cbNenhumaMeta;

    @FXML
    private DatePicker dpPrevisaoPlano;

    @FXML
    private TextArea taRelatorioPlano;

    @FXML
    private TextField tfJustificativa;

    @FXML
    private TextField tfNomeAluno;

    @FXML
    private TextField tfResponsavelPlano;

    @FXML
    private TextField tfStatus;

    @FXML
    private TextField tfTituloPlano;

    @FXML
    void onClickImprimir(ActionEvent event) {

    }

    @FXML
    void onClickLimpar(ActionEvent event) {

    }

    @FXML
    void onClickSalvar(ActionEvent event) {

    }

    @FXML
    void onClickSalvarCriar(ActionEvent event) {

    }

    @FXML
    void onClickVoltar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AndamentoPAI.fxml"));
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
