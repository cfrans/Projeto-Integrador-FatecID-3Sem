package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class RendimentoController
{

    @FXML
    private Button btLimpar;

    @FXML
    private Button btSalvar;

    @FXML
    private Button btVoltar;

    @FXML
    private CheckBox cbTipoEntrega1;

    @FXML
    private CheckBox cbTipoEntrega2;

    @FXML
    private ChoiceBox<?> chNivelParticipacao;

    @FXML
    private ChoiceBox<?> chNome;

    @FXML
    private ChoiceBox<?> chNome1;

    @FXML
    private TextArea taJustificativa;

    @FXML
    private TextField tfAtitudeAcademica;

    @FXML
    private TextField tfAvaliacao1;

    @FXML
    private TextField tfAvaliacao2;

    @FXML
    private TextField tfJustificativaPartifipacao;

    @FXML
    private TextField tfRA;

    @FXML
    private TextField tfSerieTurma;

    @FXML
    private TextField tfSimulado;

    @FXML
    void onClickLimpar(ActionEvent event) {
        System.out.println("Limpando formulário de Rendimento...");

        //Limpando ChoiceBoxes (Dropdowns)
        chNome.getSelectionModel().clearSelection();
        chNome1.getSelectionModel().clearSelection();
        chNivelParticipacao.getSelectionModel().clearSelection();

        //Limpando TextFields
        tfSerieTurma.clear();
        tfRA.clear();
        tfAvaliacao1.clear();
        tfAvaliacao2.clear();
        tfAtitudeAcademica.clear();
        tfSimulado.clear();
        tfJustificativaPartifipacao.clear();

        //Limpando CheckBoxes
        cbTipoEntrega1.setSelected(false);
        cbTipoEntrega2.setSelected(false);
        //Entender o pq nao ta pegando o item de baixo
        //cbTipoEntrega3.setSelected(false);

        //Limpando o TextArea
        taJustificativa.clear();

        //Foco no primeiro campo
        chNome.requestFocus();
    }

    @FXML
    void onClickSalvar(ActionEvent event) {
        // Chama o metodo estatico da NavegadorUtil
        System.out.println("Salvar clicado\nChamando o método estático de alerta de sucesso.");
        NavegadorUtil.exibirSucessoEVOLTAR(event, "Salvo com sucesso!",
                "Rendimento salvo com sucesso!");
    }

    @FXML
    void onClickVoltar(ActionEvent event) {
        System.out.println("Clicado em voltar.\nChamando o método estático de voltar ao menu");
        // Chama o metodo estatico da NavegadorUtil
        NavegadorUtil.voltarParaMenu(event);
    }

}
