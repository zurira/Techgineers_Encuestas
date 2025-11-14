package mx.edu.utez.encuestas.dao.impl;

import mx.edu.utez.encuestas.config.DBConnection;
import mx.edu.utez.encuestas.dao.IUsuario;
import mx.edu.utez.encuestas.model.Usuario;

import java.sql.*;

public class UsuarioDaoImpl implements IUsuario {

    @Override
    public Usuario validarLogin(String nombreUsuario, String contraseña) {
        String query = "SELECT * FROM Usuarios WHERE nombre_usuario = ? AND contraseña = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombreUsuario);
            stmt.setString(2, contraseña);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("correo"),
                        rs.getString("nombre"),
                        rs.getString("nombre_usuario"),
                        rs.getString("contraseña")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al validar login: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean existeUsuario(String nombreUsuario) {
        String query = "SELECT COUNT(*) FROM Usuarios WHERE nombre_usuario = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nombreUsuario);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar usuario: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean registrarUsuario(Usuario usuario) {
        String query = "INSERT INTO Usuarios (correo, nombre, nombre_usuario, contraseña) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usuario.getCorreo());
            stmt.setString(2, usuario.getNombre());
            stmt.setString(3, usuario.getNombreUsuario());
            stmt.setString(4, usuario.getContraseña());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
        }
        return false;
    }

    public boolean crearEncuesta(String titulo, String categoria, byte[] imagen, String estado, int creadorId) {
        String sql = "INSERT INTO Encuestas (titulo, categoria, imagen, estado, creador_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, titulo);
            stmt.setString(2, categoria);
            stmt.setBytes(3, imagen);
            stmt.setString(4, estado);
            stmt.setInt(5, creadorId);

            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Error al crear encuesta: " + e.getMessage());
            return false;
        }
    }
}