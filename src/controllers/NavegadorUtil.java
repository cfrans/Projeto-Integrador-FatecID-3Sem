package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

import static java.lang.System.exit;

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

    // Metodo temporário que fecha a aplicação. Posteriormente fazer um popup de confirmaçao
    // e talvez fazer uma opção de sair (deslogar) e outra de fechar a aplicação.
    public static void fecharAplicacao() {
        exit(0);
    }

    // Metodo que exibe uma mensagem de sucesso e volta ao Menu Principal.
    public static void exibirSucessoEVOLTAR(ActionEvent event, String titulo, String mensagem) {

        // Alerta de sucesso
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);

        // Tenta carregar a imagem de sucesso do ícone
        try {
            Image checkIcon = new Image(NavegadorUtil.class.getResourceAsStream("/resources/images/success.png"));
            if (checkIcon.isError()) {
                System.err.println("Imagem do ícone carregada, mas contém erro.");
            } else {
                ImageView imageView = new ImageView(checkIcon);
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                alert.setGraphic(imageView);
            }
        } catch (NullPointerException e) {
            System.err.println("ERRO: Ícone de sucesso não encontrado em '/resources/images/success.png'. Verifique o caminho!");
        } catch (Exception e) {
            System.err.println("Erro geral ao carregar ícone: " + e.getMessage());
        }

        // Centraliza o Alerta na janela atual
        try {
            Stage stageDono = (Stage) ((Node) event.getSource()).getScene().getWindow();
            alert.initOwner(stageDono);
        } catch (Exception e) {
            System.err.println("Falha ao definir 'owner' do alerta. Pode não aparecer centralizado.");
        }

        // Aguarda o usuário clicar em ok para continuar
        alert.showAndWait();

        // Chama o metodo estatico da NavegadorUtil para voltar ao Menu
        voltarParaMenu(event);
    }

}