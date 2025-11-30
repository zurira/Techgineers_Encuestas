package mx.edu.utez.encuestas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class DocenteApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // ¡IMPORTANTE! Nombre del FXML actualizado
        FXMLLoader fxmlLoader = new FXMLLoader(DocenteApp.class.getResource("view/PrincipalDocente.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

        stage.setTitle("Docente | Administración de Formularios");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}