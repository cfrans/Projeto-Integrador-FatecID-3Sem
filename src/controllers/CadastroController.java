package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class CadastroController {

    @FXML
    private Button btAdicionar;

    @FXML
    private Button btLimpar;

    @FXML
    private Button btSalvar;

    @FXML
    private Button btVoltar;

    @FXML
    private CheckBox cbTipoNecessidade1;

    @FXML
    private CheckBox cbTipoNecessidade2;

    @FXML
    private CheckBox cbTipoNecessidade3;

    @FXML
    private CheckBox cbTipoNecessidade4;

    @FXML
    private CheckBox cbTipoNecessidade5;

    @FXML
    private Label lbParentesco;

    @FXML
    private ChoiceBox<String> chParentesco;

    @FXML
    private ChoiceBox<?> chSerieTurma;

    @FXML
    private DatePicker dpDataLaudo;

    @FXML
    private DatePicker dpDataNascimento;

    @FXML
    private TextArea taObservacoes;

    @FXML
    private TextField tdNomeResponsavel;

    @FXML
    private TextField tfEmail;

    @FXML
    private TextField tfNome;

    @FXML
    private TextField tfNumeroLaudo;

    @FXML
    private TextField tfProfissional;

    @FXML
    private TextField tfTelefone;

    @FXML
    private Font x1;

    @FXML
    private Color x2;

    @FXML
    void onClickAdicionar(ActionEvent event) {
        System.out.println("Adicionar clicado");
    }

    @FXML
    void onClickLimpar(ActionEvent event) {
        System.out.println("Limpar clicado");
    }

    @FXML
    void onClickSalvar(ActionEvent event) {
        System.out.println("Salvar clicado");
    }

    @FXML
    void onClickVoltar(ActionEvent event) {
        System.out.println("Voltar clicado");
    }
}


