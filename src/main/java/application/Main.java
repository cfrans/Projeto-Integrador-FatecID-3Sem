package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carrega a primeira tela (Login.fxml)
        Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));

        try {
            Image icon32 = new Image(getClass().getResourceAsStream("/resources/images/icon32.png"));
            Image icon64 = new Image(getClass().getResourceAsStream("/resources/images/icon64.png"));
            Image icon128 = new Image(getClass().getResourceAsStream("/resources/images/icon128.png"));

            // Adiciona TODOS os ícones ao 'primaryStage'
            primaryStage.getIcons().addAll(icon32, icon64, icon128);

        } catch (Exception e) {
            System.err.println("Erro ao carregar ícones: " + e.getMessage());
            // A aplicação continua mesmo se o ícone falhar
        }

        primaryStage.setTitle("Sistema de Acompanhamento Escolar");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
