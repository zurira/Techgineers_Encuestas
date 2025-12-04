package mx.edu.utez.encuestas.dao.impl;

import mx.edu.utez.encuestas.config.DBConnection;
import mx.edu.utez.encuestas.dao.IUsuario;
import mx.edu.utez.encuestas.model.Respuesta;
import mx.edu.utez.encuestas.model.Rol;
import mx.edu.utez.encuestas.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDaoImpl implements IUsuario {

    @Override
    public Usuario validarLogin(String nombreUsuario, String contraseña) {
        String query = "SELECT u.id, u.correo, u.nombre, u.nombre_usuario, u.contraseña, " +
                "r.id AS rol_id, r.nombre AS rol_nombre " +
                "FROM Usuarios u " +
                "JOIN Roles r ON u.rol_id = r.id " +
                "WHERE u.nombre_usuario = ? AND u.contraseña = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombreUsuario);
            stmt.setString(2, contraseña);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Rol rol = new Rol(
                        rs.getInt("rol_id"),
                        rs.getString("rol_nombre")
                );

                Usuario usuario = new Usuario(
                        rs.getInt("id"),
                        rs.getString("correo"),
                        rs.getString("nombre"),
                        rs.getString("nombre_usuario"),
                        rs.getString("contraseña")
                );
                usuario.setRol(rol);

                return usuario;
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
    public List<Usuario> findAll() throws SQLException {
        final String sql = "SELECT * FROM Usuarios WHERE rol_id = 2 ";
        List<Usuario> docentes = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                docentes.add(mapResultSet(rs));
            }
        }
        return docentes;
    }

    private Usuario mapResultSet(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setCorreo(rs.getString("correo"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setNombreUsuario(rs.getString("nombre_usuario"));
        usuario.setContraseña(rs.getString("contraseña"));
        Rol rol = new Rol();
        rol.setId(rs.getInt("rol_id"));
        usuario.setRol(rol);
        return usuario;
    }

    @Override
    public boolean registrarUsuario(Usuario usuario) {
        String query = "INSERT INTO Usuarios (correo, nombre, nombre_usuario, contraseña, rol_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usuario.getCorreo());
            stmt.setString(2, usuario.getNombre());
            stmt.setString(3, usuario.getNombreUsuario());
            stmt.setString(4, usuario.getContraseña());
            stmt.setInt(5, usuario.getRol().getId());

            return stmt.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static int contarDocentes() {
        int total = 0;
        String query = "SELECT COUNT(*) FROM usuarios WHERE rol_id = 2";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar docentes: " + e.getMessage());
        }
        return total;
    }


}