package mx.edu.utez.encuestas.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mx.edu.utez.encuestas.HelloApplication;

import java.io.IOException;

public class modalcerrarsController {

    private Stage principalStage;

    public void setPrincipalStage(Stage principalStage) {
        this.principalStage = principalStage;
    }

    @FXML
    void onCancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void onAccept(ActionEvent event) {
        try {
            // Abre la vista de login.fxml en una nueva ventana
            FXMLLoader fxmlLoader = new FXMLLoader(
                    HelloApplication.class.getResource("/mx/edu/utez/encuestas/views/login.fxml")
            );
            Parent root = fxmlLoader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setMaximized(true);
            loginStage.setScene(new Scene(root));
            loginStage.show();

            // cierra el principal
            if (principalStage != null) {
                principalStage.close();
            }

            // cierra modal de cerrar sesión
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();


            System.out.println("Sesión cerrada correctamente, regresando a login...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}