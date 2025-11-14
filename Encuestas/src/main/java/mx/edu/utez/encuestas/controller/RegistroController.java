package mx.edu.utez.encuestas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import mx.edu.utez.encuestas.dao.IUsuario;
import mx.edu.utez.encuestas.dao.impl.UsuarioDaoImpl;
import mx.edu.utez.encuestas.model.Usuario;

public class RegistroController {

    @FXML private TextField correoField;
    @FXML private TextField nombreField;
    @FXML private TextField usuarioField;
    @FXML private PasswordField claveField;
    @FXML private Button registrarButton;

    private final IUsuario usuarioDao = new UsuarioDaoImpl();

    @FXML
    private void onRegistrar() {
        String correo = correoField.getText();
        String nombre = nombreField.getText();
        String usuario = usuarioField.getText();
        String clave = claveField.getText();

        if (correo.isEmpty() || nombre.isEmpty() || usuario.isEmpty() || clave.isEmpty()) {
            mostrarAlerta("Todos los campos son obligatorios.");
            return;
        }

        if (usuarioDao.existeUsuario(usuario)) {
            mostrarAlerta("El nombre de usuario ya existe.");
            return;
        }

        Usuario nuevo = new Usuario(correo, nombre, usuario, clave);
        if (usuarioDao.registrarUsuario(nuevo)) {
            mostrarAlerta("Registro exitoso. Ya puedes iniciar sesión.");
            limpiarCampos();
        } else {
            mostrarAlerta("Error al registrar. Intenta más tarde.");
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void limpiarCampos() {
        correoField.clear();
        nombreField.clear();
        usuarioField.clear();
        claveField.clear();
    }
}