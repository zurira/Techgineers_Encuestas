package mx.edu.utez.encuestas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import mx.edu.utez.encuestas.dao.impl.UsuarioDaoImpl;
import mx.edu.utez.encuestas.dao.impl.EncuestaDaoImpl;

public class DashboardAdminController {

    @FXML private Label lblTotalDocentes;
    @FXML private Label lblTotalEncuestas;
    @FXML private Label lblEncuestasActivas;
    @FXML private PieChart pieChartEncuestas;

    @FXML
    private void initialize() {
        cargarDashboard();
    }

    private void cargarDashboard() {
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
}