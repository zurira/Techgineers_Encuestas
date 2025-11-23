package mx.edu.utez.encuestas.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import oracle.jdbc.proxy.annotation.Pre;

import java.io.*;
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
        txtTitulo.setText(encuesta.getTitulo());
        txtCategoria.setText(encuesta.getCategoria());
        txtDescripcion.setText(encuesta.getDescripcionCorta());
        // Convertir byte[] a Image
        if (encuesta.getImagen() != null) {
            Image image = new Image(new ByteArrayInputStream(encuesta.getImagen()));
            imgPortada.setImage(image);
        } else {
            imgPortada.setImage(null);
        }

        cargarPreguntas();
    }


    @FXML
    public void agregarPregunta() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/agregarPreguntas.fxml"));
            Parent root = loader.load();

            AgregarPreguntasController controller = loader.getController();
            controller.setIdEncuesta((int)encuesta.getId());

            Stage modal = new Stage();
            modal.setScene(new Scene(root));
            modal.setTitle("Agregar pregunta");
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

    @FXML
    public void guardarEncuesta() {
        String titulo = txtTitulo.getText().trim();
        String categoria = txtCategoria.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (titulo.isEmpty() || categoria.isEmpty() || descripcion.isEmpty()){
            mostrarAlerta("Todos los campos deben estar completos.");
            return;
        }

        encuesta.setTitulo(titulo);
        encuesta.setCategoria(categoria);
        encuesta.setDescripcionCorta(descripcion);
        encuesta.setEstado("borrador");


        if (imagenSeleccionada == null) {
            mostrarAlerta("Debes seleccionar una imagen");
        } else {
            encuesta.setImagen(imagenSeleccionada);
        }

        EncuestaImpl encuestaDao = new EncuestaImpl();
        boolean resultado = (encuesta.getId() > 0)
                ? encuestaDao.actualizarEncuesta(encuesta)
                : encuestaDao.guardarEncuesta(encuesta);

        mostrarAlerta(resultado ? "Encuesta guardada correctamente." : "Error al guardar la encuesta.");
    }


    @FXML
    public void publicarEncuesta() {
        String titulo = txtTitulo.getText().trim();
        String categoria = txtCategoria.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (titulo.isEmpty() || categoria.isEmpty() || descripcion.isEmpty()){
            mostrarAlerta("Todos los campos deben estar completos.");
            return;
        }

        encuesta.setTitulo(titulo);
        encuesta.setCategoria(categoria);
        encuesta.setDescripcionCorta(descripcion);
        encuesta.setEstado("activa");


        if (imagenSeleccionada == null) {
            mostrarAlerta("Debes seleccionar una imagen");
        } else {
            encuesta.setImagen(imagenSeleccionada);
        }

        EncuestaImpl encuestaDao = new EncuestaImpl();
        boolean resultado = (encuesta.getId() > 0)
                ? encuestaDao.actualizarEncuesta(encuesta)
                : encuestaDao.guardarEncuesta(encuesta);

        mostrarAlerta(resultado ? "Encuesta guardada correctamente." : "Error al guardar la encuesta.");
    }

    @FXML
    private void seleccionarFoto() {
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
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
