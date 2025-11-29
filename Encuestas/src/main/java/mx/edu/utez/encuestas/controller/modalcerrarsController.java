package mx.edu.utez.encuestas.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class modalcerrarsController {

    @FXML
    void onCancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void onAccept(ActionEvent event) {
        // logica para cerrar
        System.out.println("Sesión cerrada correctamente");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();


    }
}
