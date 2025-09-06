package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavegadorUtil {

    private NavegadorUtil() {}

    // Metodo genérico para trocar de tela.
    public static void mudarTela(ActionEvent event, String caminhoFXML) {
        try {
            Parent root = FXMLLoader.load(NavegadorUtil.class.getResource(caminhoFXML));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erro ao tentar carregar FXML: " + caminhoFXML);
            e.printStackTrace();
        }
    }

    // Metodo específico e reutilizável para voltar ao Menu Principal.
    public static void voltarParaMenu(ActionEvent event) {
        mudarTela(event, "/view/Menu.fxml");
    }

}