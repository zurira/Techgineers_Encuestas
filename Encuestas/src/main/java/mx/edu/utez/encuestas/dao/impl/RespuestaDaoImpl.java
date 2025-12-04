package mx.edu.utez.encuestas.dao.impl;

import mx.edu.utez.encuestas.config.DBConnection;
import mx.edu.utez.encuestas.model.FrecuenciaDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;


public class RespuestaDaoImpl {

    public List<FrecuenciaDTO> frecuenciasPorEncuesta(int encuestaId) throws SQLException {
        String sql =
                "SELECT p.id AS pregunta_id, p.texto AS pregunta_texto, " +
                        "       o.id AS opcion_id, o.texto AS opcion_texto, " +
                        "       COUNT(r.id) AS total_respuestas " +
                        "FROM Preguntas p " +
                        "JOIN Opciones o ON o.pregunta_id = p.id " +
                        "LEFT JOIN Respuestas r ON r.opcion_id = o.id " +
                        "WHERE p.encuesta_id = ? " +
                        "GROUP BY p.id, p.texto, o.id, o.texto " +
                        "ORDER BY p.id, o.id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, encuestaId);
            ResultSet rs = ps.executeQuery();
            List<FrecuenciaDTO> lista = new ArrayList<>();

            while (rs.next()) {
                FrecuenciaDTO f = new FrecuenciaDTO();
                f.setPreguntaId(rs.getInt("pregunta_id"));
                f.setPregunta(rs.getString("pregunta_texto"));
                f.setOpcionId(rs.getInt("opcion_id"));
                f.setOpcion(rs.getString("opcion_texto"));
                f.setTotal(rs.getInt("total_respuestas"));
                lista.add(f);
            }

            return lista;
        }
    }




}
