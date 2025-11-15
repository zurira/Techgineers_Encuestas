package mx.edu.utez.encuestas.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mx.edu.utez.encuestas.dao.IEncuesta;
import mx.edu.utez.encuestas.dao.impl.UsuarioDaoImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import mx.edu.utez.encuestas.dao.impl.EncuestaImpl;
import mx.edu.utez.encuestas.model.Usuario;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CrearEncuestaController {

    @FXML private TextField tituloField;
    @FXML private TextField categoriaField;
    @FXML private TextField rutaImagenField;
    @FXML private CheckBox estadoCheck;
    @FXML private Button crearButton;

    private File imagenSeleccionada;
    private final UsuarioDaoImpl dao = new UsuarioDaoImpl();
    private final Usuario docen = new Usuario();
    IEncuesta encuestaDao = new EncuestaImpl();


    private final int idDocente = docen.getId();
    private String plantilla;

    public void setPlantilla(String plantilla) {
        this.plantilla = plantilla;
        System.out.println("Plantilla seleccionada: " + plantilla);
    }

    private Usuario docente;

    public void setDocente(Usuario docente) {
        this.docente = docente;
    }

    @FXML
    private void onSeleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        imagenSeleccionada = fileChooser.showOpenDialog(null);
        if (imagenSeleccionada != null) {
            rutaImagenField.setText(imagenSeleccionada.getName());
        }
    }

    @FXML
    private void onCrearEncuesta() {
        String titulo = tituloField.getText();
        String categoria = categoriaField.getText();
        String estado = estadoCheck.isSelected() ? "activa" : "inactiva";

        if (titulo.isEmpty() || categoria.isEmpty()) {
            mostrarAlerta("Título y categoría son obligatorios.");
            return;
        }

        byte[] imagenBytes = new byte[0];
        if (imagenSeleccionada != null) {
            try (FileInputStream fis = new FileInputStream(imagenSeleccionada)) {
                imagenBytes = fis.readAllBytes();
            } catch (IOException e) {
                mostrarAlerta("Error al leer la imagen.");
                return;
            }
        }

        boolean exito = dao.crearEncuesta(titulo, categoria, imagenBytes, estado, idDocente);
        if (exito) {
            mostrarAlerta("Encuesta creada exitosamente.");

            int idEncuesta = encuestaDao.obtenerUltimoIdEncuestaDelDocente(idDocente);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/agregarPreguntas.fxml"));
                Parent root = loader.load();

                AgregarPreguntasController controller = loader.getController();
                controller.setIdEncuesta(idEncuesta);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Agregar preguntas");
                stage.show();

                Stage actual = (Stage) crearButton.getScene().getWindow();
                actual.close();

            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Error al abrir la vista de preguntas.");
            }

        } else {
            mostrarAlerta("Error al crear la encuesta.");
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Crear encuesta");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void limpiarCampos() {
        tituloField.clear();
        categoriaField.clear();
        rutaImagenField.clear();
        estadoCheck.setSelected(true);
        imagenSeleccionada = null;
    }
}