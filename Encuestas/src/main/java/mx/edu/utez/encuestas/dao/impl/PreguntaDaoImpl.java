package mx.edu.utez.encuestas.dao.impl;

import mx.edu.utez.encuestas.config.DBConnection;
import mx.edu.utez.encuestas.dao.IPregunta;
import mx.edu.utez.encuestas.model.Pregunta;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PreguntaDaoImpl implements IPregunta {
    @Override
    public int insertarPregunta(String texto, int idEncuesta) {
        String sql = "INSERT INTO Preguntas (texto, encuesta_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, texto);
            stmt.setInt(2, idEncuesta);

            int rows = stmt.executeUpdate();
            if (rows == 1) {

                // recupera la ultima pregunta insertada a la encuesta
                String sql2 = "SELECT id FROM Preguntas WHERE encuesta_id = ? ORDER BY id DESC FETCH FIRST 1 ROWS ONLY";

                try (PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
                    stmt2.setInt(1, idEncuesta);
                    ResultSet rs = stmt2.executeQuery();
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar pregunta: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean actualizarPregunta(String texto, int idEncuesta, int idPregunta) {
        String sql = "UPDATE Preguntas SET texto = ?, encuesta_id = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, texto);
            stmt.setInt(2, idEncuesta);
            stmt.setInt(3, idPregunta);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar pregunta: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarPreguntaConOpciones(int idPregunta) {
        String sqlRespuestas =
                "DELETE FROM Respuestas WHERE opcion_id IN (SELECT id FROM Opciones WHERE pregunta_id = ?)";

        String sqlOpciones =
                "DELETE FROM Opciones WHERE pregunta_id = ?";

        String sqlPregunta =
                "DELETE FROM Preguntas WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // primero elimina respuestas
            try (PreparedStatement stmtRes = conn.prepareStatement(sqlRespuestas)) {
                stmtRes.setInt(1, idPregunta);
                stmtRes.executeUpdate();
            }

            //luego elimina opciones
            try (PreparedStatement stmtOp = conn.prepareStatement(sqlOpciones)) {
                stmtOp.setInt(1, idPregunta);
                stmtOp.executeUpdate();
            }

            // por ultimo elimna la pregunta
            int rowsPregunta;
            try (PreparedStatement stmtPreg = conn.prepareStatement(sqlPregunta)) {
                stmtPreg.setInt(1, idPregunta);
                rowsPregunta = stmtPreg.executeUpdate();
            }

            //se eliminan primero las entidades hijas
            conn.commit();
            return rowsPregunta == 1;

        } catch (SQLException e) {
            System.err.println("Error al eliminar pregunta con opciones/respuestas: " + e.getMessage());
            return false;
        }
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

    @Override
    public int contarPreguntasPorEncuesta(int idEncuesta) {
        String sql = "SELECT COUNT(id) FROM Preguntas WHERE encuesta_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEncuesta);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error al contar preguntas: " + e.getMessage());
        }
        return 0;
    }
}
