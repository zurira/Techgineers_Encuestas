package mx.edu.utez.encuestas.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.List;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import mx.edu.utez.encuestas.DocenteApp;
import mx.edu.utez.encuestas.dao.IEncuesta;
import mx.edu.utez.encuestas.dao.impl.EncuestaImpl;
import mx.edu.utez.encuestas.model.Encuesta;
import mx.edu.utez.encuestas.model.Usuario;

public class PrincipalDocenteController {

    @FXML private VBox contenedorEncuestas;

    private final IEncuesta encuestaDao = new EncuestaImpl();
    private Usuario usuarioActivo;

    public void setUsuarioActivo(Usuario usuario) {
        this.usuarioActivo = usuario;
        System.out.println("Usuario activo: " + usuario.getNombreUsuario());
        cargarEncuestasComoTarjetas(); // Cargar encuestas como cards
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

            // Actualiza la interfaz con las nuevas cards
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

            Label titulo = new Label(encuesta.getTitulo());
            titulo.getStyleClass().add("recent-form-title");

            Label subtitulo = new Label(encuesta.getCategoria());
            subtitulo.getStyleClass().add("recent-form-subtitle");

            Label estado = new Label("Estado: " + encuesta.getEstado());
            estado.getStyleClass().add("recent-form-time");

            // icono editar
            FontIcon editIcon = new FontIcon("fa-pencil");
            editIcon.setIconSize(18);
            editIcon.getStyleClass().add("action-icon");

            Button btnEditar = new Button();
            btnEditar.setGraphic(editIcon);
            btnEditar.getStyleClass().add("action-button");
            btnEditar.setTooltip(new Tooltip("Editar encuesta"));
            btnEditar.setOnAction(e -> abrirEditorEncuesta(encuesta));

            // cambio de estado
            FontIcon switchIcon = new FontIcon(encuesta.isActiva() ? "fa-toggle-on" : "fa-toggle-off");
            switchIcon.setIconSize(24);
            switchIcon.setIconColor(encuesta.isActiva() ? Color.GREEN : Color.GRAY);


            Button btnSwitch = new Button();
            btnSwitch.setGraphic(switchIcon);
            btnSwitch.getStyleClass().add("action-button");
            btnSwitch.setTooltip(new Tooltip("Activar/Desactivar encuesta"));
            btnSwitch.setOnAction(e -> {
                encuesta.setActiva(!encuesta.isActiva());
                switchIcon.setIconLiteral(encuesta.isActiva() ? "fa-toggle-on" : "fa-toggle-off");
                switchIcon.setIconColor(encuesta.isActiva() ? Color.GREEN : Color.GRAY);
                String nuevoEstado = encuesta.isActiva() ? "activa" : "inactiva";
                encuestaDao.actualizarEstado((int) encuesta.getId(), nuevoEstado);

                cargarEncuestasComoTarjetas();
            });

            HBox acciones = new HBox(10, btnEditar, btnSwitch);
            acciones.setAlignment(Pos.CENTER_RIGHT);

            tarjeta.getChildren().addAll(titulo, subtitulo, estado, acciones);
            contenedorEncuestas.getChildren().add(tarjeta);
        }
    }

    private void abrirEditorEncuesta(Encuesta encuesta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/vistaEncuesta.fxml"));
            Parent root = loader.load();

            VistaEncuestaController controller = loader.getController();
            controller.setEncuesta(encuesta);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editor de encuesta");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(contenedorEncuestas.getScene().getWindow());
            stage.showAndWait();

            cargarEncuestasComoTarjetas(); // Refrescar después de editar
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("No se pudo abrir la vista de la encuesta.");
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
