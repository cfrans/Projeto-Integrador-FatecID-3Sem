package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class IntervencoesController {


    @FXML
    private Button btLimpar;

    @FXML
    private Button btSalvar;

    @FXML
    private Button btVoltar;

    @FXML
    private CheckBox cbTipoIntervencao1;

    @FXML
    private CheckBox cbTipoIntervencao2;

    @FXML
    private CheckBox cbTipoIntervencao3;

    @FXML
    private CheckBox cbTipoIntervencao4;

    @FXML
    private CheckBox cbTipoIntervencao5;

    @FXML
    private ChoiceBox<?> chNome;

    @FXML
    private ChoiceBox<?> chResponsavel;

    @FXML
    private DatePicker dpDataIntervencao;

    @FXML
    private TextArea taObservacoes;

    @FXML
    private TextField tfOutroTipoIntervencao;

    @FXML
    private TextField tfRA;

    @FXML
    private TextField tfSerieTurma;

    @FXML
    private TextField tfTituloIntervencao;

    @FXML
    void onClickAdicionarIntervencao(ActionEvent event) {

    }

    @FXML
    void onClickLimpar(ActionEvent event) {
        System.out.println("Limpando formulário de Intervenções...");

        //Limpando ChoiceBoxes (Dropdowns)
        chNome.getSelectionModel().clearSelection();
        chResponsavel.getSelectionModel().clearSelection();

        //Limpando TextFields
        tfRA.clear();
        tfSerieTurma.clear();
        tfOutroTipoIntervencao.clear();

        //Limpando o DatePicker
        dpDataIntervencao.setValue(null);

        //Limpando o TextArea
        taObservacoes.clear();

        //Limpando os CheckBoxes
        cbTipoIntervencao1.setSelected(false);
        cbTipoIntervencao2.setSelected(false);
        cbTipoIntervencao3.setSelected(false);
        cbTipoIntervencao4.setSelected(false);
        cbTipoIntervencao5.setSelected(false);

        //Coloca o foco do cursor de volta no primeiro campo (o ChoiceBox do nome)
        chNome.requestFocus();
    }

    @FXML
    void onClickSalvar(ActionEvent event) {
        // Chama o metodo estatico da NavegadorUtil
        System.out.println("Salvar clicado\nChamando o método estático de alerta de sucesso.");
        NavegadorUtil.exibirSucessoEVOLTAR(event, "Salvo com sucesso!",
                "Intervenção salvo com sucesso!");
    }

    @FXML
    void onClickVoltar(ActionEvent event) {
        System.out.println("Clicado em voltar.\nChamando o método estático de voltar ao menu");
        // Chama o metodo estatico da NavegadorUtil
        NavegadorUtil.voltarParaMenu(event);
    }

}
