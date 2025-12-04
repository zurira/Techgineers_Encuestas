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
import mx.edu.utez.encuestas.dao.impl.UsuarioDaoImpl;
import mx.edu.utez.encuestas.dao.impl.EncuestaDaoImpl;
import mx.edu.utez.encuestas.model.Usuario;

import java.io.IOException;

public class DashboardAdminController {

    @FXML private Label lblTotalDocentes;
    @FXML private Label lblTotalEncuestas;
    @FXML private Label lblEncuestasActivas;
    @FXML private PieChart pieChartEncuestas;
    @FXML private Label nombreAdmin;
    @FXML private Button btnDocentes;

    private int docenteId;
    private Usuario usuarioActivo;

    public void setUsuarioActivo(Usuario usuario) {
        this.usuarioActivo = usuario;
        this.docenteId = usuario.getId();
        nombreAdmin.setText(usuario.getNombre());
        cargarDashboardDocente();
    }

    private void cargarDashboardDocente() {
        if (docenteId <= 0) {
            System.err.println("DocenteId no válido, no se cargan datos.");
            return;
        }

        //info de la bd
        int totalDocentes = UsuarioDaoImpl.contarDocentes();
        int totalEncuestas = EncuestaDaoImpl.contarEncuestas();
        int encuestasActivas = EncuestaDaoImpl.contarEncuestasPorEstado("activa");
        int encuestasBorrador = EncuestaDaoImpl.contarEncuestasPorEstado("borrador");

        // poner valores a los label
        lblTotalDocentes.setText(String.valueOf(totalDocentes));
        lblTotalEncuestas.setText(String.valueOf(totalEncuestas));
        lblEncuestasActivas.setText(String.valueOf(encuestasActivas));

        // grafico, Piechart componente necesario
        ObservableList<PieChart.Data> datos = FXCollections.observableArrayList(
                new PieChart.Data("Activas", encuestasActivas),
                new PieChart.Data("Borrador", encuestasBorrador)
        );
        pieChartEncuestas.setData(datos);
    }

    @FXML
    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/modalcerrars.fxml"));
            Parent root = loader.load();

            // se obtiene el controllador del modal
            modalcerrarsController modalController = loader.getController();

            // se pasa el stage actual al del modal
            Stage principalStage = (Stage) nombreAdmin.getScene().getWindow();
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
    private void irDocentes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/principalAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Docentes");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

            PrincipalAdminController prinAdminController = loader.getController();
            prinAdminController.setUsuarioActivo(usuarioActivo);

            //cerrar ventana
            Stage currentStage = (Stage) btnDocentes.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            System.err.println("Error al cargar la vista " + e.getMessage());
        }
    }
}