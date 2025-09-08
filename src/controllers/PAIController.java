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
        System.out.println("Limpando formulário PAI...");

        //Limpando TextFields
        tfRA.clear();
        tfMeta.clear();
        tfRecursos.clear();
        tfSerieTurma.clear();
        tfTituloPlano.clear();

        //Limpando ChoiceBoxes (Dropdowns)
        chNome.getSelectionModel().clearSelection();
        chResponsavelPlano.getSelectionModel().clearSelection();

        //Limpando o DatePicker
        dpRevisaoPlano.setValue(null);

        //Limpando o TextArea
        taDescricaoPlano.clear();

        //Coloca o foco do cursor de volta no primeiro campo
        chNome.requestFocus();
    }

    @FXML
    void onClickSalvar(ActionEvent event) {
        // Chama o metodo estatico da NavegadorUtil
        System.out.println("Salvar clicado\nChamando o método estático de alerta de sucesso.");
        NavegadorUtil.exibirSucessoEVOLTAR(event, "Salvo com sucesso!",
                "Plano de Acompanhamento Individual salvo com sucesso!");
    }

    @FXML
    void onClickVoltar(ActionEvent event) {
        System.out.println("Clicado em voltar.\nChamando o método estático de voltar ao menu");
        // Chama o metodo estatico da NavegadorUtil
        NavegadorUtil.voltarParaMenu(event);
    }

}
