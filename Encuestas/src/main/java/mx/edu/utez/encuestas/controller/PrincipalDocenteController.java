package mx.edu.utez.encuestas.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.util.List;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import mx.edu.utez.encuestas.DocenteApp;
import mx.edu.utez.encuestas.dao.IEncuestaDao;
import mx.edu.utez.encuestas.dao.impl.EncuestaDaoImpl;
import mx.edu.utez.encuestas.model.Encuesta;
import mx.edu.utez.encuestas.model.Usuario;

public class PrincipalDocenteController {

    @FXML private VBox contenedorEncuestas;

    private final IEncuestaDao encuestaDao = new EncuestaDaoImpl();
    private Usuario usuarioActivo;

    public void setUsuarioActivo(Usuario usuario) {
        this.usuarioActivo = usuario;
        System.out.println("Usuario activo: " + usuario.getNombreUsuario());
        cargarEncuestasComoTarjetas(); // Cargar encuestas del docente al recibir el usuario
    }

    @FXML
    private void crearFormularioEnBlanco(MouseEvent event) {
        System.out.println("Crear Formulario en Blanco presionado. Cargando vista del editor...");

        try {
            FXMLLoader loader = new FXMLLoader(DocenteApp.class.getResource("views/crearEncuesta.fxml"));
            Parent root = loader.load();

            CrearEncuestaController controller = loader.getController();
            controller.setPlantilla("Blank");
            controller.setDocente(usuarioActivo); // Pasar el usuario activo

            Stage modalStage = new Stage();
            modalStage.setScene(new Scene(root));
            modalStage.setTitle("Crear nueva encuesta");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            modalStage.showAndWait();

            // 🔄 Refrescar tabla después de cerrar el modal
            cargarEncuestasComoTarjetas();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar la vista del editor de formularios.");
        }
    }

    private void cargarEncuestasComoTarjetas() {
        contenedorEncuestas.getChildren().clear();

        List<Encuesta> encuestas = encuestaDao.obtenerEncuestasPorDocente(usuarioActivo.getId());

        for (Encuesta encuesta : encuestas) {
            VBox tarjeta = new VBox();
            tarjeta.getStyleClass().add("recent-form-box");
            tarjeta.setSpacing(5);
            tarjeta.setPadding(new Insets(10));
            tarjeta.setCursor(Cursor.HAND);

            Label titulo = new Label(encuesta.getTitulo());
            titulo.getStyleClass().add("recent-form-title");

            Label subtitulo = new Label(encuesta.getCategoria());
            subtitulo.getStyleClass().add("recent-form-subtitle");

            Label estado = new Label("Estado: " + encuesta.getEstado());
            estado.getStyleClass().add("recent-form-time");

            tarjeta.getChildren().addAll(titulo, subtitulo, estado);

            // 🔗 Acción al hacer clic en la tarjeta
            tarjeta.setOnMouseClicked(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/agregar_preguntas.fxml"));
                    Parent root = loader.load();

                    AgregarPreguntasController controller = loader.getController();
                    controller.setIdEncuesta(encuesta.getId());

                    Stage modal = new Stage();
                    modal.setScene(new Scene(root));
                    modal.setTitle("Agregar preguntas");
                    modal.initModality(Modality.APPLICATION_MODAL);
                    modal.initOwner(((Node) event.getSource()).getScene().getWindow());
                    modal.showAndWait();

                } catch (IOException e) {
                    e.printStackTrace();
                    mostrarAlerta("Error al abrir la vista de preguntas.");
                }
            });

            contenedorEncuestas.getChildren().add(tarjeta);
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Panel docente");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
