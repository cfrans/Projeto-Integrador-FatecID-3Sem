package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.security.cert.PolicyNode;
import java.util.ResourceBundle;

public class SobreController extends BaseController implements Initializable {

    @FXML
    private Hyperlink linkRepositorio;
    @FXML
    private TextFlow txtFuncionalidades;

    @FXML
    private void abrirRepositorio() {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/cfrans/Projeto-Integrador-FatecID-3Sem.git"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}