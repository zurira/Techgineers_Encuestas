package mx.edu.utez.encuestas.controller;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
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
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.List;

public class AgregarPreguntasController {

    @FXML private TextArea preguntaField;
    @FXML private VBox opcionesBox;

    private final PreguntaDaoImpl dao = new PreguntaDaoImpl();
    private final OpcionDaoImpl daoOp = new OpcionDaoImpl();
    private int idEncuesta;
    private Pregunta preguntaExistente;

    private List<TextField> camposOpciones = new ArrayList<>();

    public void setIdEncuesta(int idEncuesta) {
        this.idEncuesta = idEncuesta;
    }

    public void setPreguntaParaEditar(Pregunta pregunta) {
        this.preguntaExistente = pregunta;
        preguntaField.setText(pregunta.getTexto());

        // limpia la interfaz
        opcionesBox.getChildren().clear();
        camposOpciones.clear();

        List<Opcion> opciones = daoOp.obtenerOpcionesPorPregunta(pregunta.getId());
        for (Opcion opcion : opciones) {
            HBox fila = new HBox(10);
            fila.getStyleClass().add("opcion-row");

            TextField campo = new TextField(opcion.getTexto());
            campo.setPromptText("Opción de respuesta");
            campo.setPrefWidth(300);
            campo.getStyleClass().add("text-field");
            camposOpciones.add(campo);

            Button btnAgregar = new Button();
            btnAgregar.setGraphic(new FontIcon("fa-plus"));
            btnAgregar.getStyleClass().add("opcion-action");
            btnAgregar.setOnAction(e -> onAgregarOpcion());

            Button btnEliminar = new Button();
            btnEliminar.setGraphic(new FontIcon("fa-trash"));
            btnEliminar.getStyleClass().add("opcion-action");
            btnEliminar.setOnAction(e -> {
                opcionesBox.getChildren().remove(fila);
                camposOpciones.remove(campo);
            });

            HBox accionesBox = new HBox(6, btnAgregar, btnEliminar);
            accionesBox.setAlignment(Pos.CENTER_RIGHT);

            fila.getChildren().addAll(campo, accionesBox);
            opcionesBox.getChildren().add(fila);
        }

        // se añaden 2 opciones por default
        while (camposOpciones.size() < 2) {
            onAgregarOpcion();
        }
    }

    @FXML
    public void initialize() {
        onAgregarOpcion();
        onAgregarOpcion();
    }

    @FXML
    private void onAgregarOpcion() {
        HBox fila = new HBox(10);
        fila.getStyleClass().add("opcion-row");

        TextField opcion = new TextField();
        opcion.setPromptText("Opción de respuesta");
        opcion.setPrefWidth(300);
        opcion.getStyleClass().add("text-field");
        camposOpciones.add(opcion);

        Button btnAgregar = new Button();
        btnAgregar.setGraphic(new FontIcon("fa-plus"));
        btnAgregar.getStyleClass().add("opcion-action");
        btnAgregar.setOnAction(e -> onAgregarOpcion());

        Button btnEliminar = new Button();
        btnEliminar.setGraphic(new FontIcon("fa-trash"));
        btnEliminar.getStyleClass().add("opcion-action");
        btnEliminar.setOnAction(e -> {
            opcionesBox.getChildren().remove(fila);
            camposOpciones.remove(opcion);
        });

        HBox accionesBox = new HBox(6, btnAgregar, btnEliminar);
        accionesBox.setAlignment(Pos.CENTER_RIGHT);

        fila.getChildren().addAll(opcion, accionesBox);
        opcionesBox.getChildren().add(fila);
    }


    @FXML
    private void onGuardarPregunta() {
        String textoPregunta = preguntaField.getText().trim();
        if (textoPregunta.isEmpty()) {
            mostrarAlerta("La pregunta no puede estar vacía.");
            return;
        }

        List<String> opciones = new ArrayList<>();
        for (TextField tf : camposOpciones) {
            if (!tf.getText().trim().isEmpty()) {
                opciones.add(tf.getText().trim());
            }
        }

        if (opciones.size() < 2) {
            mostrarAlerta("Agrega al menos dos opciones.");
            return;
        }

        if (preguntaExistente != null) {
            // Editar pregunta existente
            preguntaExistente.setTexto(textoPregunta);
            boolean actualizada = dao.actualizarPregunta(
                    preguntaExistente.getTexto(),
                    this.idEncuesta, // se usa el ID ya establecido
                    preguntaExistente.getId()
            );

            if (actualizada) {
                daoOp.eliminarOpcionesPorPregunta(preguntaExistente.getId());
                for (String opcion : opciones) {
                    daoOp.insertarOpcion(opcion, preguntaExistente.getId());
                }
                mostrarAlerta("Pregunta actualizada.");
                cerrarVentana();
            } else {
                mostrarAlerta("Error al actualizar la pregunta.");
            }

        } else {
            // Crear nueva pregunta
            int idPregunta = dao.insertarPregunta(textoPregunta, idEncuesta);
            if (idPregunta > 0) {
                for (String opcion : opciones) {
                    daoOp.insertarOpcion(opcion, idPregunta);
                }
                mostrarAlerta("Pregunta guardada.");
                cerrarVentana();
            } else {
                mostrarAlerta("Error al guardar la pregunta.");
            }
        }
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

    private void cerrarVentana() {
        Stage stage = (Stage) preguntaField.getScene().getWindow();
        stage.close();
    }
}

