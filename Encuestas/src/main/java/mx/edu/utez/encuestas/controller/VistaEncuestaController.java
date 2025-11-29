package mx.edu.utez.encuestas.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.edu.utez.encuestas.dao.impl.EncuestaImpl;
import mx.edu.utez.encuestas.dao.impl.OpcionDaoImpl;
import mx.edu.utez.encuestas.dao.impl.PreguntaDaoImpl;
import mx.edu.utez.encuestas.model.Encuesta;
import mx.edu.utez.encuestas.model.Opcion;
import mx.edu.utez.encuestas.model.Pregunta;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class VistaEncuestaController {
    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescripcion;
    @FXML private VBox contenedorPreguntas;
    @FXML private Button btnSeleccionarImagen;
    @FXML private ImageView imgPortada;
    @FXML private TextField txtCategoria;

    private byte[] imagenSeleccionada;

    private final PreguntaDaoImpl dao = new PreguntaDaoImpl();
    private final OpcionDaoImpl daoOp = new OpcionDaoImpl();
    private Encuesta encuesta;

    public void setEncuesta(Encuesta encuesta) {
        this.encuesta = encuesta;
        inicializarDatos();
    }

    private void inicializarDatos() {
        if (encuesta != null) {
            txtTitulo.setText(encuesta.getTitulo());
            txtCategoria.setText(encuesta.getCategoria());
            txtDescripcion.setText(encuesta.getDescripcionCorta() != null ? encuesta.getDescripcionCorta() : "");

            cargarImagenPortada(encuesta.getImagen());
            System.out.println("ID de la encuesta cargada: " + encuesta.getId());
            cargarPreguntas();
        }
    }

    private void cargarImagenPortada(byte[] imagenBytes) {
        if (imagenBytes != null && imagenBytes.length > 0) {
            Image image = new Image(new ByteArrayInputStream(imagenBytes));
            imgPortada.setImage(image);
        } else {
            imgPortada.setImage(null);
        }
    }

    @FXML
    public void agregarPregunta() {
        abrirEditorPregunta(null);
    }

    private void abrirEditorPregunta(Pregunta pregunta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/agregarPreguntas.fxml"));
            Parent root = loader.load();

            AgregarPreguntasController controller = loader.getController();
            controller.setIdEncuesta(encuesta.getId());

            if (pregunta != null) {
                controller.setPreguntaParaEditar(pregunta);
            }

            Stage modal = new Stage();
            modal.setScene(new Scene(root));
            modal.setTitle(pregunta != null ? "Editar pregunta" : "Agregar pregunta");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(txtTitulo.getScene().getWindow());
            modal.showAndWait();

            cargarPreguntas();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("No se pudo abrir el editor de preguntas.");
        }
    }


    private void cargarPreguntas() {
        contenedorPreguntas.getChildren().clear();

        List<Pregunta> preguntas = dao.obtenerPreguntasPorEncuesta(encuesta.getId());

        for (Pregunta pregunta : preguntas) {
            VBox tarjeta = new VBox();
            tarjeta.getStyleClass().add("pregunta-card");
            tarjeta.setSpacing(8);
            tarjeta.setPadding(new Insets(10));

            //boton de eliminar y editar
            HBox accionesBox = new HBox(10);
            accionesBox.setAlignment(Pos.CENTER_RIGHT);

            Button btnEliminar = new Button();
            btnEliminar.setGraphic(new FontIcon("fa-trash"));
            btnEliminar.setOnAction(e -> {
                eliminarPregunta(pregunta);
            });

            Button btnEditar = new Button();
            btnEditar.setGraphic(new FontIcon("fa-pencil"));
            btnEditar.setOnAction(e -> abrirEditorPregunta(pregunta));

            accionesBox.getChildren().addAll(btnEditar, btnEliminar);

            Label lblPregunta = new Label(pregunta.getTexto());
            lblPregunta.getStyleClass().add("pregunta-titulo");

            VBox opcionesBox = new VBox();
            opcionesBox.setSpacing(5);

            List<Opcion> opciones = daoOp.obtenerOpcionesPorPregunta(pregunta.getId());
            for (Opcion opcion : opciones) {
                CheckBox check = new CheckBox(opcion.getTexto());
                check.setDisable(true);
                opcionesBox.getChildren().add(check);
            }

            tarjeta.setOnMouseClicked(e -> abrirEditorPregunta(pregunta));

            tarjeta.getChildren().addAll(accionesBox, lblPregunta, opcionesBox);

            contenedorPreguntas.getChildren().add(tarjeta);
        }
    }

    @FXML
    public void guardarEncuesta() {
        String titulo = txtTitulo.getText().trim();
        String categoria = txtCategoria.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (titulo.isEmpty() || categoria.isEmpty() || descripcion.isEmpty()) {
            mostrarAlerta("Todos los campos deben estar completos.");
            return;
        }

        encuesta.setTitulo(titulo);
        encuesta.setCategoria(categoria);
        encuesta.setDescripcionCorta(descripcion);
        encuesta.setEstado(Encuesta.EstadoEncuesta.borrador);

        //encuesta.setImagen(imagenSeleccionada != null ? imagenSeleccionada : new byte[0]);

        EncuestaImpl encuestaDao = new EncuestaImpl();
        boolean resultado;
        if (encuesta.getId() > 0) {
            resultado = encuestaDao.actualizarEncuesta(encuesta);
        } else {
            int idGenerado = encuestaDao.guardarEncuesta(encuesta);
            if (idGenerado > 0) {
                encuesta.setId(idGenerado); // asigna el id de la encuesta, esto se ocupa para cragra las preguntas
                resultado = true;
            } else {
                resultado = false;
            }
        }

        mostrarAlerta(resultado ? "Encuesta guardada correctamente." : "Error al guardar la encuesta.");
    }

    @FXML
    public void publicarEncuesta() {
        String titulo = txtTitulo.getText().trim();
        String categoria = txtCategoria.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (titulo.isEmpty() || categoria.isEmpty() || descripcion.isEmpty()) {
            mostrarAlerta("Todos los campos deben estar completos.");
            return;
        }

        encuesta.setTitulo(titulo);
        encuesta.setCategoria(categoria);
        encuesta.setDescripcionCorta(descripcion);
        encuesta.setEstado(Encuesta.EstadoEncuesta.activa);

        //encuesta.setImagen(imagenSeleccionada != null ? imagenSeleccionada : new byte[0]); // evita nulos

        EncuestaImpl encuestaDao = new EncuestaImpl();
        boolean resultado;
        if (encuesta.getId() > 0) {
            resultado = encuestaDao.actualizarEncuesta(encuesta);
        } else {
            int idGenerado = encuestaDao.guardarEncuesta(encuesta);
            if (idGenerado > 0) {
                encuesta.setId(idGenerado); // asigna el id de la encuesta, esto se ocupa para cragra las preguntas
                resultado = true;
            } else {
                resultado = false;
            }
        }

        mostrarAlerta(resultado ? "Encuesta publicada correctamente." : "Error al publicar la encuesta.");
    }

    private void eliminarPregunta(Pregunta pregunta) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro de que quieres eliminar la pregunta: '" + pregunta.getTexto() + "'? Se eliminarán todas sus opciones asociadas.", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("Eliminar pregunta");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                int idPregunta = pregunta.getId();
                //se eliminan todas las opciones asociadas a las preguntas
                daoOp.eliminarOpcionesPorPregunta(idPregunta);

                //se elimina la pregunta
                boolean preguntaEliminada = dao.eliminarPregunta(idPregunta);

                if (preguntaEliminada) {
                    mostrarAlerta("Pregunta eliminada correctamente.");
                    cargarPreguntas();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar la pregunta", "Ocurrió un error al intentar eliminar la pregunta.");
                }
            }
        });
    }

    @FXML
    private void seleccionarPortada() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg")
        );
        Stage stage = (Stage) btnSeleccionarImagen.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                byte[] bytes = Files.readAllBytes(selectedFile.toPath());
                encuesta.setImagen(bytes);
                imagenSeleccionada = bytes;

                Image image = new Image(new ByteArrayInputStream(bytes));
                imgPortada.setImage(image);

                System.out.println("Imagen seleccionada: " + selectedFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la imagen", "El archivo de imagen no se encontró.");
            }
        }
    }

    private void mostrarAlerta(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Encuesta");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}