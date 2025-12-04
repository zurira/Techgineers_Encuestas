package mx.edu.utez.encuestas.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import mx.edu.utez.encuestas.config.DBConnection;
import mx.edu.utez.encuestas.dao.impl.RespuestaDaoImpl;
import mx.edu.utez.encuestas.model.FrecuenciaDTO;
import mx.edu.utez.encuestas.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportesController {

    @FXML private BarChart<String, Number> barChart;
    @FXML private ComboBox<EncuestaOption> cmbEncuestas;

    private final RespuestaDaoImpl respuestaDao = new RespuestaDaoImpl();
    private Usuario usuarioActivo;

    // Clase interna para mostrar títulos en el ComboBox
    public static class EncuestaOption {
        private final int id;
        private final String titulo;
        public EncuestaOption(int id, String titulo) { this.id = id; this.titulo = titulo; }
        public int getId() { return id; }
        public String getTitulo() { return titulo; }
        @Override public String toString() { return titulo; } // lo que se muestra en el ComboBox
    }

    @FXML
    public void initialize() {
        // Solo configurar el listener, no cargar encuestas aún
        cmbEncuestas.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                cargarDatos(newV.getId());
            }
        });
    }
    @FXML private Label lblTituloEncuesta;



    private void cargarDatos(int encuestaId) {
        try {
            List<FrecuenciaDTO> datos = respuestaDao.frecuenciasPorEncuesta(encuestaId);

            Map<String, XYChart.Series<String, Number>> seriesMap = new LinkedHashMap<>();
            for (FrecuenciaDTO f : datos) {
                seriesMap.putIfAbsent(f.getPregunta(), new XYChart.Series<>());
                XYChart.Series<String, Number> seriePregunta = seriesMap.get(f.getPregunta());
                seriePregunta.setName(f.getPregunta());
                seriePregunta.getData().add(new XYChart.Data<>(f.getOpcion(), f.getTotal()));
            }

            barChart.getData().clear();
            barChart.getData().addAll(seriesMap.values());

            // Mostrar título de la encuesta seleccionada
            EncuestaOption opt = cmbEncuestas.getValue();
            if (opt != null) {
                lblTituloEncuesta.setText("Encuesta: " + opt.getTitulo());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setUsuarioActivo(Usuario usuario) {
        this.usuarioActivo = usuario;
        System.out.println("Usuario activo en reportes: " + usuario.getNombreUsuario());
        cargarEncuestas(); // ahora sí, con el usuario activo listo
    }
    private void cargarEncuestas() {
        if (usuarioActivo == null) return;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT id, titulo FROM Encuestas WHERE creador_id = ? ORDER BY id")) {
            ps.setInt(1, usuarioActivo.getId());

            try (ResultSet rs = ps.executeQuery()) {
                cmbEncuestas.getItems().clear();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String titulo = rs.getString("titulo");
                    cmbEncuestas.getItems().add(new EncuestaOption(id, titulo));
                }
            }

            if (!cmbEncuestas.getItems().isEmpty()) {
                cmbEncuestas.getSelectionModel().selectFirst();
                cargarDatos(cmbEncuestas.getValue().getId());
            } else {
                barChart.getData().clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void actualizarGrafico() {
        EncuestaOption opt = cmbEncuestas.getValue();
        if (opt != null) {
            cargarDatos(opt.getId());
        }
    }



}
