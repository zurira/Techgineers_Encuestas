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
}