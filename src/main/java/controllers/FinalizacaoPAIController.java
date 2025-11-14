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
import util.ControladorNavegavel;

import java.io.IOException;

public class FinalizacaoPAIController implements ControladorNavegavel {

    // Variável para guardar o controlador principal
    private MenuController menuController;

    // Método obrigatório da interface
    @Override
    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }

    @FXML private Button btImprimir;
    @FXML private Button btLmpar;
    @FXML private Button btSalvar;
    @FXML private Button btSalvarCriar;
    @FXML private Button btVoltar;
    @FXML private CheckBox cbMeta1;
    @FXML private CheckBox cbMeta2;
    @FXML private CheckBox cbMeta3;
    @FXML private CheckBox cbMeta4;
    @FXML private CheckBox cbNenhumaMeta;
    @FXML private DatePicker dpPrevisaoPlano;
    @FXML private TextArea taRelatorioPlano;
    @FXML private TextField tfJustificativa;
    @FXML private TextField tfNomeAluno;
    @FXML private TextField tfResponsavelPlano;
    @FXML private TextField tfStatus;
    @FXML private TextField tfTituloPlano;

    @FXML void onClickImprimir(ActionEvent event) { }
    @FXML void onClickLimpar(ActionEvent event) { }
    @FXML void onClickSalvar(ActionEvent event) { }
    @FXML void onClickSalvarCriar(ActionEvent event) { }

    @FXML
    void onClickVoltar(ActionEvent event) {
        if (menuController != null) {
            menuController.navegarPara("/view/AndamentoPAI.fxml");
        } else {
            System.err.println("Erro: MenuController não foi injetado!");
        }
    }
}