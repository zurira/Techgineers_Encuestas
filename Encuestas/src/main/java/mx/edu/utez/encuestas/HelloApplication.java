package mx.edu.utez.encuestas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/mx/edu/utez/encuestas/views/login.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("BilioTech");
        stage.setScene(scene);
        // Esta línea maximiza la ventana.
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}