package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ConsultaRendimentoController {

    @FXML
    private Button btImprimir;

    @FXML
    private Button btVoltar;

    @FXML
    private ChoiceBox<?> chNome;

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

    @FXML
    void onClickImprimir(ActionEvent event) {

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
