package mx.edu.utez.encuestas;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }



    @FXML
    private void abrirModalCerrarSesion() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("/mx/edu/utez/encuestas/modalcerrars.fxml")
        );
        Parent root = fxmlLoader.load();

        Stage stage = new Stage();
        stage.setTitle("Cerrar sesión");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL); // bloquea la ventana principal
        stage.showAndWait();
    }





}