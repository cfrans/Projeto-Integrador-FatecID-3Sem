package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class ConsultaRendimentoController extends BaseController  {

    @FXML
    private Button btImprimir;

    @FXML
    private Button btVoltar;

    @FXML
    private ChoiceBox<?> chNome;

    @FXML
    private DatePicker dpDataFinal;

    @FXML
    private DatePicker dpDataInicio;

    @FXML
    private TextField tfMateria1;

    @FXML
    private TextField tfMateria2;

    @FXML
    private TextField tfMateria3;

    @FXML
    private TextField tfMateria4;

    @FXML
    private TextField tfNota1;

    @FXML
    private TextField tfNota2;

    @FXML
    private TextField tfNota3;

    @FXML
    private TextField tfNota4;

    @FXML
    private TextField tfPorcentagemMetas;

    @FXML
    private TextField tfQuantidadeFinalizados;

    @FXML
    private TextField tfQuantidadeIntervencoes;

    @FXML
    private TextField tfQunatidadePlanos;

    @FXML
    private TextField tfRA;

    @FXML
    private TextField tfResponsavelFrequente;

    @FXML
    private TextField tfSerieTurma;

    @FXML
    private TextField tfTipoFrequente;

    /**
     * Método que é utilizado para carregar o funcionamento do botão imprimir
     * após a realização do event
     * @param event
     */
    @FXML
    void onClickImprimir(ActionEvent event) {

    }

}
