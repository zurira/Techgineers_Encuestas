package mx.edu.utez.encuestas.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mx.edu.utez.encuestas.dao.impl.UsuarioDaoImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import mx.edu.utez.encuestas.model.Usuario;
import mx.edu.utez.encuestas.dao.IUsuario;

import java.io.IOException;

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

    @FXML
    private void onRegistrar(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/registro.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Registro de cuenta");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar la vista de registro: " + e.getMessage());
        }

    }
}