package mx.edu.utez.encuestas.controller;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
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
    @FXML private Button btnGuardarPregunta;

    private final PreguntaDaoImpl dao = new PreguntaDaoImpl();
    private final OpcionDaoImpl daoOp = new OpcionDaoImpl();
    private int idEncuesta;
    private Pregunta preguntaExistente;

    private static class OpcionUI {
        final TextField campo;
        Integer id;

        OpcionUI(TextField campo, Integer id) {
            this.campo = campo;
            this.id = id;
        }
    }

    private final List<OpcionUI> camposOpciones = new ArrayList<>();

    public void setIdEncuesta(int idEncuesta) {
        this.idEncuesta = idEncuesta;
    }

    public void setPreguntaParaEditar(Pregunta pregunta) {
        this.preguntaExistente = pregunta;
        preguntaField.setText(pregunta.getTexto());

        //limpia la interfaz
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

            // Guarda id existente
            camposOpciones.add(new OpcionUI(campo, opcion.getId()));

            Button btnAgregar = new Button();
            btnAgregar.setGraphic(new FontIcon("fa-plus"));
            btnAgregar.getStyleClass().add("opcion-action");
            btnAgregar.setOnAction(e -> onAgregarOpcion());

            Button btnEliminar = new Button();
            btnEliminar.setGraphic(new FontIcon("fa-trash"));
            btnEliminar.getStyleClass().add("opcion-action");
            btnEliminar.setOnAction(e -> {
                opcionesBox.getChildren().remove(fila);
                camposOpciones.removeIf(ou -> ou.campo == campo);
            });

            HBox accionesBox = new HBox(6, btnAgregar, btnEliminar);
            accionesBox.setAlignment(Pos.CENTER_RIGHT);

            fila.getChildren().addAll(campo, accionesBox);
            opcionesBox.getChildren().add(fila);
        }

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

        TextField campo = new TextField();
        campo.setPromptText("Opción de respuesta");
        campo.setPrefWidth(300);
        campo.getStyleClass().add("text-field");

        camposOpciones.add(new OpcionUI(campo, null));

        Button btnAgregar = new Button();
        btnAgregar.setGraphic(new FontIcon("fa-plus"));
        btnAgregar.getStyleClass().add("opcion-action");
        btnAgregar.setOnAction(e -> onAgregarOpcion());

        Button btnEliminar = new Button();
        btnEliminar.setGraphic(new FontIcon("fa-trash"));
        btnEliminar.getStyleClass().add("opcion-action");
        btnEliminar.setOnAction(e -> {
            opcionesBox.getChildren().remove(fila);
            camposOpciones.removeIf(ou -> ou.campo == campo);
        });

        HBox accionesBox = new HBox(6, btnAgregar, btnEliminar);
        accionesBox.setAlignment(Pos.CENTER_RIGHT);

        fila.getChildren().addAll(campo, accionesBox);
        opcionesBox.getChildren().add(fila);
    }


    @FXML
    private void onGuardarPregunta() {
        String textoPregunta = preguntaField.getText().trim();
        if (textoPregunta.isEmpty()) { mostrarAlerta("La pregunta no puede estar vacía."); return; }

        // limpia opciones
        List<OpcionUI> opcionesUI = camposOpciones.stream()
                .filter(ou -> ou.campo.getText() != null && !ou.campo.getText().trim().isEmpty())
                .toList();

        if (opcionesUI.size() < 2) {
            mostrarAlerta("Agrega al menos dos opciones.");
            return;
        }

        if (preguntaExistente != null) {
            // Actualiza la pregunta
            boolean actualizada = dao.actualizarPregunta(textoPregunta, this.idEncuesta, preguntaExistente.getId());
            if (!actualizada) { mostrarAlerta("Error al actualizar la pregunta."); return; }

            // Obtiene las opciones
            List<Opcion> opcionesBD = daoOp.obtenerOpcionesPorPregunta(preguntaExistente.getId());
            java.util.Map<Integer, Opcion> mapaBD = new java.util.HashMap<>();
            for (Opcion o : opcionesBD) mapaBD.put(o.getId(), o);

            boolean opcionDuplicada = false;

            //Actualiza o inserta
            for (OpcionUI ou : opcionesUI) {
                String nuevoTexto = ou.campo.getText().trim();

                if (ou.id == null) {
                    int resultado = daoOp.insertarOpcion(nuevoTexto, preguntaExistente.getId());
                    if (resultado == -2) {
                        opcionDuplicada = true;
                    }
                } else {
                    Opcion existente = mapaBD.get(ou.id);
                    if (existente != null && !existente.getTexto().equals(nuevoTexto)) {
                        daoOp.actualizarOpcion(ou.id, nuevoTexto);
                    }
                    mapaBD.remove(ou.id);
                }
            }

            // Elimina opciones no usadas
            for (Opcion aEliminar : mapaBD.values()) {
                daoOp.eliminarOpcionPorId(aEliminar.getId());
            }
            if(opcionDuplicada) {
                mostrarAlerta("Pregunta actualizada. Se omitieron algunas opciones porque estaban duplicadas");
            } else {
                mostrarAlerta("Pregunta actualizada.");
            }
            cerrarVentana();
        } else {
            int idPregunta = dao.insertarPregunta(textoPregunta, idEncuesta);
            if (idPregunta > 0) {
                boolean opcionDuplicada = false;
                for (OpcionUI ou : opcionesUI) {
                    int resultado = daoOp.insertarOpcion(ou.campo.getText().trim(), idPregunta);
                    if (resultado == -2) {
                        opcionDuplicada = true;
                    }
                }
                if(opcionDuplicada) {
                    mostrarAlerta("Pregunta guardada, pero algunas opciones no se guardaron porque ya existían.");
                } else {
                    mostrarAlerta("Pregunta guardada.");
                }
                cerrarVentana();
            } else {
                mostrarAlerta("Error al guardar la pregunta.");
            }
        }
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