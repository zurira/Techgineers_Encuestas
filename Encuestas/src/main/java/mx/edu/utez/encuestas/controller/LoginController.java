package mx.edu.utez.encuestas.controller;

import mx.edu.utez.encuestas.dao.impl.UsuarioDaoImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import mx.edu.utez.encuestas.model.Usuario;
import mx.edu.utez.encuestas.dao.IUsuario;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink forgotLink;

    private final IUsuario usuarioDao = new UsuarioDaoImpl();

    @FXML
    private void onLogin() {
        String usuario = usernameField.getText();
        String clave = passwordField.getText();

        Usuario u = usuarioDao.validarLogin(usuario, clave);
        if (u != null) {
            System.out.println("Bienvenido, " + u.getNombre());
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de autenticación");
            alert.setHeaderText(null);
            alert.setContentText("Usuario o contraseña incorrectos.");
            alert.showAndWait();
        }
    }
}