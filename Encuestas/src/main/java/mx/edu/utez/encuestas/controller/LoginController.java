package mx.edu.utez.encuestas.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
    private void onLogin(ActionEvent event) {
        String usuario = usernameField.getText();
        String clave = passwordField.getText();

        // Validar usuario en la base de datos
        Usuario usuarioValido = usuarioDao.validarLogin(usuario, clave);

        if (usuarioValido != null) {
            System.out.println("Inicio de sesión exitoso. Redirigiendo al panel del docente...");

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/principalDocente.fxml"));
                Parent root = loader.load();

                // Si quieres pasar el usuario al panel, puedes hacerlo así:
                PrincipalDocenteController controller = loader.getController();
                controller.setUsuarioActivo(usuarioValido); // ← método opcional

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root, 1000, 700));
                stage.setTitle("Panel Docente");
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Error al cargar el panel del docente.");
            }

        } else {
            mostrarAlerta("Usuario o contraseña incorrectos.");
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Inicio de sesión");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
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