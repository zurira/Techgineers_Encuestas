package mx.edu.utez.encuestas.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
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
import mx.edu.utez.encuestas.dao.IEncuesta;
import mx.edu.utez.encuestas.dao.impl.EncuestaDaoImpl;
import mx.edu.utez.encuestas.model.Encuesta;
import mx.edu.utez.encuestas.model.Usuario;

public class PrincipalDocenteController {

    @FXML private FlowPane contenedorEncuestas;
    @FXML private TextField buscarField;
    @FXML private Label nombreDocente;
    @FXML private Button btnDashboard;

    private final IEncuesta encuestaDao = new EncuestaDaoImpl();
    private Usuario usuarioActivo;

    public void setUsuarioActivo(Usuario usuario) {
        this.usuarioActivo = usuario;
        System.out.println("Usuario activo: " + usuario.getNombre());
        nombreDocente.setText(usuario.getNombre());
        cargarEncuestasComoTarjetas(); // Carga encuestas como cards
    }

    @FXML
    public void initialize(){
        buscarField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarEncuestas(newValue);
        });
    }

    @FXML
    private void crearFormularioEnBlanco(MouseEvent event) {
        System.out.println("Crear Formulario en Blanco presionado. Cargando vista de encuesta...");

        try {
            // Crear encuesta en blanco
            Encuesta nuevaEncuesta = new Encuesta();
            nuevaEncuesta.setTitulo("Formulario sin título");
            nuevaEncuesta.setDescripcionCorta("Descripción del formulario");
            nuevaEncuesta.setEstado(Encuesta.EstadoEncuesta.borrador);
            nuevaEncuesta.setCreadorId(usuarioActivo.getId());
            nuevaEncuesta.setId(0);

            // carga la vista de encuesta
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/vistaEncuesta.fxml"));
            Parent root = loader.load();

            VistaEncuestaController controller = loader.getController();
            controller.setEncuesta(nuevaEncuesta);

            Stage modalStage = new Stage();
            modalStage.setScene(new Scene(root));
            modalStage.setTitle("Agregar encuesta");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            modalStage.showAndWait();


            cargarEncuestasComoTarjetas();



        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar la vista de encuesta.");
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

            HBox acciones = new HBox(10);
            acciones.setAlignment(Pos.CENTER_RIGHT);


            // Solo mostrar switch si la encuesta NO está en borrador
            if (encuesta.getEstado() != Encuesta.EstadoEncuesta.borrador) {
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
                    encuestaDao.actualizarEstado(encuesta.getId(), nuevoEstado);

                    cargarEncuestasComoTarjetas();
                });

                acciones.getChildren().add(btnSwitch);
            }

            tarjeta.getChildren().addAll(titulo, subtitulo, estado, acciones);
            contenedorEncuestas.getChildren().add(tarjeta);

            tarjeta.setOnMouseClicked(e -> abrirEditorEncuesta(encuesta));
            tarjeta.setCursor(Cursor.HAND);

        }
    }

    private void abrirEditorEncuesta(Encuesta encuesta) {
        try {
            // se verifica si la encuesta existe, si si se carga la info
            Encuesta encuestaCompleta = encuesta;
            if (encuesta.getId() > 0) {
                Encuesta dbEncuesta = encuestaDao.obtenerEncuestaCompletaPorId(encuesta.getId());
                if (dbEncuesta != null) {
                    encuestaCompleta = dbEncuesta;
                }
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/vistaEncuesta.fxml"));
            Parent root = loader.load();

            VistaEncuestaController controller = loader.getController();
            controller.setEncuesta(encuestaCompleta);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editor de encuesta");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(contenedorEncuestas.getScene().getWindow());
            stage.showAndWait();

            cargarEncuestasComoTarjetas();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("No se pudo abrir la vista de la encuesta.");
        }
    }

    private void filtrarEncuestas(String filtro) {
        contenedorEncuestas.getChildren().clear();

        List<Encuesta> encuestas = encuestaDao.obtenerEncuestasPorDocente(usuarioActivo.getId());

        for (Encuesta encuesta : encuestas) {
            // si el título o categoría contiene el texto buscado sin importar mayusculas y minusculas
            if (filtro == null || filtro.isEmpty() ||
                    encuesta.getTitulo().toLowerCase().contains(filtro.toLowerCase()) ||
                    encuesta.getCategoria().toLowerCase().contains(filtro.toLowerCase())) {

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

                tarjeta.getChildren().addAll(titulo, subtitulo, estado);
                contenedorEncuestas.getChildren().add(tarjeta);

                tarjeta.setOnMouseClicked(e -> abrirEditorEncuesta(encuesta));
                tarjeta.setCursor(Cursor.HAND);
            }
        }
    }

    @FXML
    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/modalcerrars.fxml"));
            Parent root = loader.load();

            // se obtiene el controllador del modal
            modalcerrarsController modalController = loader.getController();

            // se pasa el stage actual al del modal
            Stage principalStage = (Stage) nombreDocente.getScene().getWindow();
            modalController.setPrincipalStage(principalStage);

            Stage stage = new Stage();
            stage.setTitle("Cerrar sesión");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar la vista " + e.getMessage());
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Panel docente");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void irDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/dashboardDocente.fxml"));
            Parent root = loader.load();

            // obtiene el controlador del dashboard
            DashboardDocenteController dashboardController = loader.getController();
            dashboardController.setUsuarioActivo(usuarioActivo);

            Stage stage = new Stage();
            stage.setTitle("Dashboard");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

            // cerrar ventana actual
            Stage currentStage = (Stage) btnDashboard.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            System.err.println("Error al cargar la vista " + e.getMessage());
        }
    }
}