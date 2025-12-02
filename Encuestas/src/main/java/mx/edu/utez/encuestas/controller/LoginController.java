package mx.edu.utez.encuestas.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
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

        //valida usuario en la base de datos
        Usuario usuarioValido = usuarioDao.validarLogin(usuario, clave);

        if (usuarioValido != null) {
            System.out.println("Inicio de sesión exitoso");

            try {
                FXMLLoader loader = null;

                switch (usuarioValido.getRol().getNombre().trim().toLowerCase()) {
                    case "administrador":
                        System.out.println("Cargando vista admin");
                        loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/.fxml"));
                        break;
                    case "docente":
                        System.out.println("Cargando vista de docente");
                        loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/principalDocente.fxml"));
                        break;
                    default:
                        mostrarAlerta("Rol no reconocido");
                        return;
                }

                Region root = loader.load();
                //pasa el usuario al controllador
                Object controller = loader.getController();
                if (controller instanceof PrincipalDocenteController docenteController) {
                    docenteController.setUsuarioActivo(usuarioValido);
                }

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setMaximized(true);
                stage.setScene(scene);
                stage.show();

                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                stage.setX(screenBounds.getMinX());
                stage.setY(screenBounds.getMinY());
                stage.setWidth(screenBounds.getWidth());
                stage.setHeight(screenBounds.getHeight());

            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Error al cargar el panel.");
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