package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FinalizacaoPAIController extends BaseController {

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
}