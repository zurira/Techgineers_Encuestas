package mx.edu.utez.encuestas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import mx.edu.utez.encuestas.dao.impl.EncuestaDaoImpl;
import mx.edu.utez.encuestas.model.Usuario;

import java.io.IOException;

public class DashboardDocenteController {

    @FXML private Label lblTotalEncuestasDocente;
    @FXML private Label lblEncuestasActivasDocente;
    @FXML private Label lblEncuestasBorradorDocente;

    @FXML private Label nombreDocente;
    @FXML private Button btnEncuestas;
    @FXML private Button btnReportes;

    private int docenteId;
    private Usuario usuarioActivo;

    public void setUsuarioActivo(Usuario usuario) {
        this.usuarioActivo = usuario;
        this.docenteId = usuario.getId();
        nombreDocente.setText(usuario.getNombre()); // mostrar nombre en la UI
        cargarDashboardDocente();
    }

    private void cargarDashboardDocente() {

        int total = EncuestaDaoImpl.contarEncuestasPorDocente(docenteId);
        int activas = EncuestaDaoImpl.contarEncuestasPorDocenteEstado(docenteId, "activa");
        int borrador = EncuestaDaoImpl.contarEncuestasPorDocenteEstado(docenteId, "borrador");

        lblTotalEncuestasDocente.setText(String.valueOf(total));
        lblEncuestasActivasDocente.setText(String.valueOf(activas));
        lblEncuestasBorradorDocente.setText(String.valueOf(borrador));
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

    @FXML
    private void irEncuesta() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/PrincipalDocente.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Encuesta");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

            PrincipalDocenteController principalController = loader.getController();
            principalController.setUsuarioActivo(usuarioActivo);

            //cerrar ventana
            Stage currentStage = (Stage) btnEncuestas.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            System.err.println("Error al cargar la vista " + e.getMessage());
        }
    }

    @FXML
    private void irReportes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/reportes.fxml"));
            Parent root = loader.load();

            // obtiene el controlador del dashboard
            ReportesController reportController = loader.getController();
            reportController.setUsuarioActivo(usuarioActivo);

            Stage stage = new Stage();
            stage.setTitle("Dashboard");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

            // cerrar ventana actual
            Stage currentStage = (Stage) btnReportes.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            System.err.println("Error al cargar la vista " + e.getMessage());
        }
    }
}
