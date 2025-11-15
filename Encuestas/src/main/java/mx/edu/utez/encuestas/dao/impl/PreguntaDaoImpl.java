package mx.edu.utez.encuestas.dao.impl;

import mx.edu.utez.encuestas.config.DBConnection;
import mx.edu.utez.encuestas.dao.IPregunta;
import mx.edu.utez.encuestas.model.Pregunta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PreguntaDaoImpl implements IPregunta {
    @Override
    public int insertarPregunta(String texto, int idEncuesta) {
        String sql = "INSERT INTO Preguntas (texto, encuesta_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, texto);
            stmt.setInt(2, idEncuesta);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.err.println("Error al insertar pregunta: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public List<Pregunta> obtenerPreguntasPorEncuesta(int idEncuesta) {
        List<Pregunta> lista = new ArrayList<>();
        String sql = "SELECT id, texto FROM Preguntas WHERE encuesta_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEncuesta);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(new Pregunta(rs.getInt("id"), rs.getString("texto")));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener preguntas: " + e.getMessage());
        }

        return lista;
    }
}
