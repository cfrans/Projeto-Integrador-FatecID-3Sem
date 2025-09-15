package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.Objects;

public class CadastroController {

    @FXML
    private Button btAdicionar;

    @FXML
    private Button btAdicionarResponsavel;

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
        System.out.println("Limpando formulário de cadastro...");

        //Limpando TextFields
        tfNome.clear();
        tfNumeroLaudo.clear();
        tfProfissional.clear();
        tdNomeResponsavel.clear();
        tfTelefone.clear();
        tfEmail.clear();

        //Limpando DatePickers (Datas)
        dpDataNascimento.setValue(null);
        dpDataLaudo.setValue(null);

        //Limpando ChoiceBoxes (Dropdowns)
        chSerieTurma.getSelectionModel().clearSelection();
        chParentesco.getSelectionModel().clearSelection();

        //Limpando CheckBoxes (Necessidades)
        cbTipoNecessidade1.setSelected(false);
        cbTipoNecessidade2.setSelected(false);
        cbTipoNecessidade3.setSelected(false);
        cbTipoNecessidade4.setSelected(false);
        cbTipoNecessidade5.setSelected(false);

        //Limpando TextArea (Observações)
        taObservacoes.clear();

        //Coloca o cursor de volta no primeiro campo
        tfNome.requestFocus();
    }

    @FXML
    void onClickSalvar(ActionEvent event) {
        // Chama o metodo estatico da NavegadorUtil
        System.out.println("Salvar clicado\nChamando o método estático de alerta de sucesso.");
        NavegadorUtil.exibirSucessoEVOLTAR(event, "Salvo com sucesso!",
                "Aluno/Responsável salvo com sucesso!");
    }

    @FXML
    void onClickVoltar(ActionEvent event) {
        // Chama o metodo estatico da NavegadorUtil
        System.out.println("Clicado em voltar.\nChamando o método estático de voltar ao menu.");
        NavegadorUtil.voltarParaMenu(event);
    }

    private void mudarTela(ActionEvent event, String caminhoFXML) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource(caminhoFXML));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    }
}


