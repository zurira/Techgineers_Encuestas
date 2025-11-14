package mx.edu.utez.encuestas.dao;

import mx.edu.utez.encuestas.model.Usuario;

public interface IUsuario {
    Usuario validarLogin(String nombreUsuario, String contraseña);
}
