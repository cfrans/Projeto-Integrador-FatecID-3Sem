package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PAIController {

    @FXML
    private Button btAdicionarMeta;

    @FXML
    private Button btLimpar;

    @FXML
    private Button btSalvar;

    @FXML
    private Button btVoltar;

    @FXML
    private ChoiceBox<?> chNome;

    @FXML
    private ChoiceBox<?> chResponsavelPlano;

    @FXML
    private DatePicker dpRevisaoPlano;

    @FXML
    private TextArea taDescricaoPlano;

    @FXML
    private TextField tfMeta;

    @FXML
    private TextField tfRA;

    @FXML
    private TextField tfRecursos;

    @FXML
    private TextField tfSerieTurma;

    @FXML
    private TextField tfTituloPlano;

    @FXML
    void onClickAdicionarMeta(ActionEvent event) {

    }

    @FXML
    void onClickLimpar(ActionEvent event) {

    }

    @FXML
    void onClickSalvar(ActionEvent event) {

    }

    @FXML
    void onClickVoltar(ActionEvent event) {
        System.out.println("Clicado em voltar.\nChamando o método estático de voltar ao menu");
        // Chama o metodo estatico da NavegadorUtil
        NavegadorUtil.voltarParaMenu(event);
    }

}
