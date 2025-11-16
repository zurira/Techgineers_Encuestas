package mx.edu.utez.encuestas.controller;

import mx.edu.utez.encuestas.dao.impl.EncuestaImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.stage.Stage;
import mx.edu.utez.encuestas.dao.impl.OpcionDaoImpl;
import mx.edu.utez.encuestas.dao.impl.PreguntaDaoImpl;
import mx.edu.utez.encuestas.model.Opcion;
import mx.edu.utez.encuestas.model.Pregunta;

import java.util.ArrayList;
import java.util.List;

public class AgregarPreguntasController {

    @FXML private TextArea preguntaField;
    @FXML private VBox opcionesBox;

    private final PreguntaDaoImpl dao = new PreguntaDaoImpl();
    private final OpcionDaoImpl daoOp = new OpcionDaoImpl();
    private int idEncuesta;
    private Pregunta preguntaExistente;

    public void setIdEncuesta(int idEncuesta) {
        this.idEncuesta = idEncuesta;
    }

    public void setPreguntaParaEditar(Pregunta pregunta) {
        this.preguntaExistente = pregunta;
        preguntaField.setText(pregunta.getTexto());

        List<Opcion> opciones = daoOp.obtenerOpcionesPorPregunta(pregunta.getId());
        for (Opcion opcion : opciones) {
            TextField campo = new TextField(opcion.getTexto());
            opcionesBox.getChildren().add(campo);
        }
    }

    @FXML
    public void initialize() {
        onAgregarOpcion();
        onAgregarOpcion();
    }

    @FXML
    private void onAgregarOpcion() {
        TextField opcion = new TextField();
        opcion.setPromptText("Opción de respuesta");
        opcion.setPrefWidth(400);
        opcionesBox.getChildren().add(opcion);
    }

    @FXML
    private void onGuardarPregunta() {
        String textoPregunta = preguntaField.getText();
        if (textoPregunta.isEmpty()) {
            mostrarAlerta("La pregunta no puede estar vacía.");
            return;
        }

        List<String> opciones = new ArrayList<>();
        for (Node node : opcionesBox.getChildren()) {
            if (node instanceof TextField tf && !tf.getText().isEmpty()) {
                opciones.add(tf.getText());
            }
        }

        if (opciones.size() < 2) {
            mostrarAlerta("Agrega al menos dos opciones.");
            return;
        }

        if (preguntaExistente != null) {
            // Mdetecta si se abrira la ventana para editar
            preguntaExistente.setTexto(textoPregunta);
            boolean actualizada = dao.actualizarPregunta(preguntaExistente.getTexto(), preguntaExistente.getEncuestaId(), preguntaExistente.getId());

            if (actualizada) {
                daoOp.eliminarOpcionesPorPregunta(preguntaExistente.getId());
                for (String opcion : opciones) {
                    daoOp.insertarOpcion(opcion, preguntaExistente.getId());
                }
                mostrarAlerta("Pregunta actualizada.");
                //cerrarVentana();
            } else {
                mostrarAlerta("Error al actualizar la pregunta.");
            }

        } else {
            // detecta si la ventana se abrira para crar una pregunta
            int idPregunta = dao.insertarPregunta(textoPregunta, idEncuesta);
            if (idPregunta > 0) {
                for (String opcion : opciones) {
                    daoOp.insertarOpcion(opcion, idPregunta);
                }
                mostrarAlerta("Pregunta guardada.");
                limpiarCampos();
            } else {
                mostrarAlerta("Error al guardar la pregunta.");
            }
        }
    }

    @FXML
    private void onFinalizar() {
        Stage stage = (Stage) preguntaField.getScene().getWindow();
        stage.close();
    }

    private void limpiarCampos() {
        preguntaField.clear();
        opcionesBox.getChildren().clear();
        onAgregarOpcion();
        onAgregarOpcion();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Agregar pregunta");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
