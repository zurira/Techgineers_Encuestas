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
import mx.edu.utez.encuestas.dao.impl.EncuestaDaoImpl;
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
    @FXML private Button btnpublicar;
    @FXML private Button btnGuardar;

    private byte[] imagenSeleccionada;

    private final PreguntaDaoImpl dao = new PreguntaDaoImpl();
    private final OpcionDaoImpl daoOp = new OpcionDaoImpl();
    private Encuesta encuesta;

    public void setEncuesta(Encuesta encuesta) {
        this.encuesta = encuesta;
        inicializarDatos();
    }

    private void deshabilitarEdicion() {
        txtTitulo.setEditable(false);
        txtCategoria.setEditable(false);
        txtDescripcion.setEditable(false);

        btnGuardar.setDisable(true);
        btnpublicar.setDisable(true);
        btnSeleccionarImagen.setDisable(true);

        // Deshabilitar edición de preguntas existentes
        for (var nodo : contenedorPreguntas.getChildren()) {
            nodo.setDisable(true);
        }
    }

    private void inicializarDatos() {
        if (encuesta != null) {
            txtTitulo.setText(encuesta.getTitulo());
            txtCategoria.setText(encuesta.getCategoria());
            txtDescripcion.setText(encuesta.getDescripcionCorta() != null ? encuesta.getDescripcionCorta() : "");

            cargarImagenPortada(encuesta.getImagen());
            System.out.println("ID de la encuesta cargada: " + encuesta.getId());
            cargarPreguntas();




            //solo aparecera el boton de publicar para encuestas en borrador
            if (encuesta.getEstado() == Encuesta.EstadoEncuesta.borrador || encuesta.getEstado()==null) {
                btnpublicar.setVisible(true);
            } else {
                btnpublicar.setVisible(false);
            }
            if (encuesta.getId() <= 0) {
                btnpublicar.setVisible(false);
                return;
            }

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
        if (preguntas == null || preguntas.isEmpty()) {
            return; // No hay preguntas, no pasa nada
        }

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
            btnEliminar.getStyleClass().add("pregunta-action");
            btnEliminar.setOnAction(e -> eliminarPregunta(pregunta));

            Button btnEditar = new Button();
            btnEditar.setGraphic(new FontIcon("fa-pencil"));
            btnEditar.getStyleClass().add("pregunta-action");
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

            tarjeta.getChildren().addAll(lblPregunta, opcionesBox, accionesBox);
            contenedorPreguntas.getChildren().add(tarjeta);
        }
    }

    @FXML
    public void guardarEncuesta() {
        String titulo = txtTitulo.getText().trim();
        String categoria = txtCategoria.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (titulo.isEmpty() || categoria.isEmpty() || descripcion.isEmpty() || imagenSeleccionada==null) {
            mostrarAlerta("Todos los campos deben estar completos.");
            return;
        }

        if (encuesta.getId() > 0) {
            //si ya hay imagen se conserva la misma
            if (imagenSeleccionada == null || imagenSeleccionada.length == 0) {
                imagenSeleccionada = encuesta.getImagen();
            }
        } else {
            // si se agrega una encuesta nueva debe ser obligatoria
            if (imagenSeleccionada == null || imagenSeleccionada.length == 0) {
                mostrarAlerta("Debes seleccionar una imagen para la encuesta.");
                return;
            }
        }

        encuesta.setTitulo(titulo);
        encuesta.setCategoria(categoria);
        encuesta.setDescripcionCorta(descripcion);
        encuesta.setEstado(Encuesta.EstadoEncuesta.borrador);
        encuesta.setImagen(imagenSeleccionada);

        EncuestaDaoImpl encuestaDao = new EncuestaDaoImpl();
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

        //cerrar ventana
        Stage currentStage = (Stage) btnGuardar.getScene().getWindow();
        currentStage.close();
    }


    @FXML
    public void publicarEncuesta() {
        String titulo = txtTitulo.getText().trim();
        String categoria = txtCategoria.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        // validación de campos
        if (titulo.isEmpty() || categoria.isEmpty() || descripcion.isEmpty()) {
            mostrarAlerta("Debes llenas todos los campos");
            return;
        }

        //validación de imagen
        if (encuesta.getId() > 0) {
            //si ya hay imagen se conserva la misma
            if (imagenSeleccionada == null || imagenSeleccionada.length == 0) {
                imagenSeleccionada = encuesta.getImagen();
            }
        } else {
            // si se agrega una encuesta nueva debe ser obligatoria
            if (imagenSeleccionada == null || imagenSeleccionada.length == 0) {
                mostrarAlerta("Debes seleccionar una imagen para la encuesta.");
                return;
            }
        }

        // se valida si la encuesta ya esta guardada para poder publicarla
        if (encuesta.getId() <= 0) {
            mostrarAlerta("Tienes que guardar la encuesta, antes de publicar");
            return;
        }

        // se valida que por lo menos haya 3 preguntas
        int numPreguntas = dao.contarPreguntasPorEncuesta(encuesta.getId());

        if (numPreguntas < 3) {
            mostrarAlerta("La encuesta debe tener al menos 3 preguntas para ser publicada");
            return; // se termina el proceso
        }

        //valida un máximo de preguntas de 10
        if (numPreguntas > 10) {
            mostrarAlerta("La encuesta solo puede tener un máximo de 10 preguntas");
            return; // se termina el proceso
        }

        // se actualiza la encuesta y el estado cambia a activo
        encuesta.setTitulo(titulo);
        encuesta.setCategoria(categoria);
        encuesta.setDescripcionCorta(descripcion);
        encuesta.setEstado(Encuesta.EstadoEncuesta.activa);
        //no se setea la imagen, ya que puede que haya cambiado o no

        EncuestaDaoImpl encuestaDao = new EncuestaDaoImpl();
        boolean resultado;

        // actualiza en la base de datos y detecta si se cambio la imagen
        resultado = encuestaDao.actualizarEncuesta(encuesta);

        if (resultado) {
            // si se hizo la actualización correcta
            mostrarAlerta("Encuesta publicada correctamente.");
        } else {
            // si no se actualiza
            mostrarAlerta("Error al publicar la encuesta");
        }

        // se cierra modal
        Stage stage = (Stage) btnpublicar.getScene().getWindow();
        stage.close();
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
                boolean preguntaEliminada = dao.eliminarPreguntaConOpciones(pregunta.getId());

                if (preguntaEliminada) {
                    mostrarAlerta("Pregunta eliminada correctamente.");
                    cargarPreguntas();
                } else {
                    mostrarAlerta("Error al eliminar la pregunta");
                }
                cargarPreguntas();
                inicializarDatos();
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
                mostrarAlerta("Error al cargar la imagen");
            }
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Encuesta");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}