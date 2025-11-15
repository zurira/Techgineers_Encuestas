package mx.edu.utez.encuestas.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.edu.utez.encuestas.dao.impl.EncuestaImpl;
import mx.edu.utez.encuestas.dao.impl.OpcionDaoImpl;
import mx.edu.utez.encuestas.dao.impl.PreguntaDaoImpl;
import mx.edu.utez.encuestas.model.Encuesta;
import mx.edu.utez.encuestas.model.Opcion;
import mx.edu.utez.encuestas.model.Pregunta;
import oracle.jdbc.proxy.annotation.Pre;

import java.io.IOException;
import java.util.List;

public class VistaEncuestaController {
    @FXML
    private Label lblTitulo;
    @FXML private VBox contenedorPreguntas;

    private final PreguntaDaoImpl dao = new PreguntaDaoImpl();
    private final OpcionDaoImpl daoOp = new OpcionDaoImpl();
    private Encuesta encuesta;

    public void setEncuesta(Encuesta encuesta) {
        this.encuesta = encuesta;
        lblTitulo.setText(encuesta.getTitulo());
        cargarPreguntas();
    }

    @FXML
    public void abrirModalAgregarPregunta() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/agregarPreguntas.fxml"));
            Parent root = loader.load();

            AgregarPreguntasController controller = loader.getController();
            controller.setIdEncuesta((int)encuesta.getId());

            Stage modal = new Stage();
            modal.setScene(new Scene(root));
            modal.setTitle("Agregar pregunta");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(lblTitulo.getScene().getWindow());
            modal.showAndWait();

            cargarPreguntas(); // recarga después de agregar
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("No se pudo abrir el editor de preguntas.");
        }
    }

    private void cargarPreguntas() {
        contenedorPreguntas.getChildren().clear();

        List<Pregunta> preguntas = dao.obtenerPreguntasPorEncuesta((int)encuesta.getId());

        for (Pregunta pregunta : preguntas) {
            VBox tarjeta = new VBox();
            tarjeta.getStyleClass().add("pregunta-card");
            tarjeta.setSpacing(8);
            tarjeta.setPadding(new Insets(10));

            Label lblPregunta = new Label(pregunta.getTexto());
            lblPregunta.getStyleClass().add("pregunta-titulo");

            VBox opcionesBox = new VBox();
            opcionesBox.setSpacing(5);

            List<Opcion> opciones = daoOp.obtenerOpcionesPorPregunta(pregunta.getId());
            for (Opcion opcion : opciones) {
                CheckBox check = new CheckBox(opcion.getTexto());
                check.setDisable(true); // solo visual
                opcionesBox.getChildren().add(check);
            }

            tarjeta.getChildren().addAll(lblPregunta, opcionesBox);
            contenedorPreguntas.getChildren().add(tarjeta);
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
