package mx.edu.utez.encuestas.dao.impl;

import mx.edu.utez.encuestas.config.DBConnection;
import mx.edu.utez.encuestas.dao.IOpcion;
import mx.edu.utez.encuestas.dao.IPregunta;
import mx.edu.utez.encuestas.model.Opcion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OpcionDaoImpl implements IOpcion {
    @Override
    public boolean insertarOpcion(String texto, int idPregunta) {
        String sql = "INSERT INTO Opciones (texto, pregunta_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, texto);
            stmt.setInt(2, idPregunta);
            return stmt.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error al insertar opción: " + e.getMessage());
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
