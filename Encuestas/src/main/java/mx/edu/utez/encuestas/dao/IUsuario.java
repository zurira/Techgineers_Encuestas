package mx.edu.utez.encuestas.dao;

import mx.edu.utez.encuestas.model.Usuario;

import java.sql.SQLException;
import java.util.List;

public interface IUsuario {
    Usuario validarLogin(String nombreUsuario, String contraseña);
    boolean registrarUsuario(Usuario usuario);
    boolean existeUsuario(String nombreUsuario);
    public List<Usuario> findAll() throws SQLException;
}
