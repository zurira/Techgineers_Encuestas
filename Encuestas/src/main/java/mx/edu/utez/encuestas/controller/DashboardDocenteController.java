package mx.edu.utez.encuestas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import mx.edu.utez.encuestas.dao.impl.EncuestaDaoImpl;

public class DashboardDocenteController {

    @FXML private Label lblTotalEncuestasDocente;
    @FXML private Label lblEncuestasActivasDocente;
    @FXML private Label lblEncuestasBorradorDocente;

    @FXML private Label nombreDocente;

    private int docenteId;

    @FXML
    private void initialize() {
        cargarDashboardDocente();
    }

    public void setDocenteId(int id) {
        this.docenteId = id;
        cargarDashboardDocente(); //
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
    }
}
