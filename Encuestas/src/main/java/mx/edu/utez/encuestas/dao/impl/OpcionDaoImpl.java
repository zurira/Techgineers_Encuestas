package mx.edu.utez.encuestas.dao.impl;

import mx.edu.utez.encuestas.config.DBConnection;
import mx.edu.utez.encuestas.dao.IOpcion;
import mx.edu.utez.encuestas.model.Opcion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OpcionDaoImpl implements IOpcion {
    @Override
    public boolean existeOpcionConTexto(String texto, int preguntaId) {
        String sql = "SELECT COUNT(*) FROM Opciones WHERE UPPER(texto) = UPPER(?) AND pregunta_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, texto);
            stmt.setInt(2, preguntaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de opción: " + e.getMessage());
        }
        return false;
    }

    @Override
    public int insertarOpcion(String texto, int preguntaId) {
        if (existeOpcionConTexto(texto, preguntaId)) {
            System.out.println("Opcion ya existente");
            return -2;
        }
        String sql = "INSERT INTO Opciones (texto, pregunta_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, texto);
            stmt.setInt(2, preguntaId);

            int rows = stmt.executeUpdate();
            if (rows == 1) {
                String sql2 = "SELECT id FROM Opciones WHERE pregunta_id = ? ORDER BY id DESC FETCH FIRST 1 ROWS ONLY";
                try (PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
                    stmt2.setInt(1, preguntaId);
                    ResultSet rs = stmt2.executeQuery();
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar opción: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean eliminarOpcionesPorPregunta(int idPregunta) {
        String sql = "DELETE FROM Opciones WHERE pregunta_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPregunta);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar opciones: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarOpcionPorId(int idOpcion) {
        String sqlRespuestas = "DELETE FROM Respuestas WHERE opcion_id = ?";
        String sqlOpcion = "DELETE FROM Opciones WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            // borra las respuestas ligadas
            try (PreparedStatement stmtRes = conn.prepareStatement(sqlRespuestas)) {
                stmtRes.setInt(1, idOpcion);
                stmtRes.executeUpdate();
            }
            // borra la opción
            try (PreparedStatement stmtOp = conn.prepareStatement(sqlOpcion)) {
                stmtOp.setInt(1, idOpcion);
                return stmtOp.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar opción y respuestas: " + e.getMessage());
            return false;
        }
    }

    public int contarRespuestasPorOpcion(int idOpcion) {
        String sql = "SELECT COUNT(*) FROM Respuestas WHERE opcion_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idOpcion);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.err.println("Error al contar respuestas: " + e.getMessage());
            return -1;
        }
    }


    public boolean actualizarOpcion(int idOpcion, String nuevoTexto) {
        String sql = "UPDATE Opciones SET texto = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuevoTexto);
            stmt.setInt(2, idOpcion);
            boolean exito = stmt.executeUpdate() == 1;
            if (exito) {
                System.out.println("Opcion actualizada correctamente");
            }
            return exito;
        } catch (SQLException e) {
            System.err.println("Error al actualizar opción: " + e.getMessage());
            return false;
        }
    }


    @Override
    public List<Opcion> obtenerOpcionesPorPregunta(int idPregunta) {
        List<Opcion> lista = new ArrayList<>();
        String sql = "SELECT id, texto FROM Opciones WHERE pregunta_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPregunta);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(new Opcion(rs.getInt("id"), rs.getString("texto")));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener opciones: " + e.getMessage());
        }

        return lista;
    }
}