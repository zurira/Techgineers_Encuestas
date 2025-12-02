package mx.edu.utez.encuestas.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mx.edu.utez.encuestas.dao.impl.RespuestaDaoImpl;
import mx.edu.utez.encuestas.model.Encuesta;
import mx.edu.utez.encuestas.model.Pregunta;
import mx.edu.utez.encuestas.model.Opcion;
import mx.edu.utez.encuestas.model.Respuesta;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class EncuestaModalController implements Initializable {

    @FXML private Label lblTituloEncuesta;
    @FXML private TextField txtNombreAlumno;
    @FXML private TextField txtGrupoAlumno;
    @FXML private VBox preguntasContainer;
    @FXML private Button btnEnviarRespuestas;

    private Encuesta encuestaSeleccionada;
    private final RespuestaDaoImpl respuestaDao = new RespuestaDaoImpl();

    // Mapa para guardar las opciones seleccionadas por pregunta
    private final Map<Pregunta, ToggleGroup> preguntaToggleGroups = new HashMap<>();

    public void setEncuesta(Encuesta encuesta) {
        this.encuestaSeleccionada = encuesta;
        lblTituloEncuesta.setText(encuesta.getTitulo());
        cargarPreguntas();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnEnviarRespuestas.setOnAction(e -> registrarRespuestas());
    }

    private void cargarPreguntas() {
        try {
            List<Pregunta> preguntas = encuestaSeleccionada.getPreguntas();
            preguntasContainer.getChildren().clear();

            for (Pregunta pregunta : preguntas) {
                Label lblPregunta = new Label(pregunta.getTexto());
                lblPregunta.getStyleClass().add("pregunta-label");

                VBox opcionesBox = new VBox(8);
                ToggleGroup group = new ToggleGroup();

                for (Opcion opcion : pregunta.getOpciones()) {
                    RadioButton rb = new RadioButton(opcion.getTexto());
                    rb.setUserData(opcion);
                    rb.setToggleGroup(group);
                    rb.getStyleClass().add("radio-button");
                    opcionesBox.getChildren().add(rb);
                }

                preguntaToggleGroups.put(pregunta, group);

                VBox preguntaBox = new VBox(10, lblPregunta, opcionesBox);
                preguntaBox.getStyleClass().add("pregunta-box");
                preguntasContainer.getChildren().add(preguntaBox);
            }
        } catch (Exception e) {
            preguntasContainer.getChildren().add(new Label("Error al cargar preguntas: " + e.getMessage()));
        }
    }

    private void registrarRespuestas() {
        String nombre = txtNombreAlumno.getText();
        String grupo = txtGrupoAlumno.getText();

        if (nombre.isBlank() || grupo.isBlank()) {
            mostrarAlerta("Debes ingresar tu nombre y grupo.");
            return;
        }

        try {
            for (Map.Entry<Pregunta, ToggleGroup> entry : preguntaToggleGroups.entrySet()) {
                ToggleGroup group = entry.getValue();
                if (group.getSelectedToggle() != null) {
                    Opcion opcionSeleccionada = (Opcion) group.getSelectedToggle().getUserData();
                    Respuesta respuesta = new Respuesta(nombre, grupo, opcionSeleccionada.getId());
                    respuestaDao.guardarRespuesta(respuesta);
                }
            }
            mostrarAlerta("Respuestas registradas correctamente.");
            cerrarModal();
        } catch (SQLException e) {
            mostrarAlerta("Error al guardar respuestas: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Encuesta");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrarModal() {
        Stage stage = (Stage) btnEnviarRespuestas.getScene().getWindow();
        stage.close();
    }
}