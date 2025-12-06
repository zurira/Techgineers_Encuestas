package mx.edu.utez.encuestas.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;
import mx.edu.utez.encuestas.dao.IUsuario;
import mx.edu.utez.encuestas.dao.impl.UsuarioDaoImpl;
import mx.edu.utez.encuestas.model.Usuario;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private Button togglePasswordBtn;
    @FXML private Button btnReturn;

    private final IUsuario usuarioDao = new UsuarioDaoImpl();

    @FXML
    private void onLogin(ActionEvent event) {
        String usuario = usernameField.getText();
        String clave = obtenerPassword();

        if (usuario.isEmpty() || clave.isEmpty()) {
            mostrarAlerta("Los campos de usuario y contraseña no pueden estar vacíos.");
            return;
        }

        //valida credenciales
        Usuario usuarioValido = usuarioDao.validarLogin(usuario, clave);

        if (usuarioValido == null) {
            mostrarAlerta("Usuario o contraseña incorrectos.");
            return;
        }

        cargarDashboardSegunRol(usuarioValido, event);
    }

    private String obtenerPassword() {
        return txtPassword.isVisible()
                ? txtPassword.getText()
                : txtPasswordVisible.getText();
    }


    private void cargarDashboardSegunRol(Usuario usuarioValido, ActionEvent event) {
        try {
            FXMLLoader loader;

            String rol = usuarioValido.getRol().getNombre().trim().toLowerCase();

            switch (rol) {
                case "administrador" -> loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/dashboardAdmin.fxml"));

                case "docente" -> loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/dashboardDocente.fxml"));

                default -> {
                    mostrarAlerta("Rol no reconocido.");
                    return;
                }
            }

            Region root = loader.load();
            Object controller = loader.getController();

            // asigna usuario al controlador correspondiente
            if (controller instanceof DashboardAdminController adminController) {
                adminController.setUsuarioActivo(usuarioValido);
            } else if (controller instanceof DashboardDocenteController docenteController) {
                docenteController.setUsuarioActivo(usuarioValido);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

            ajustarPantalla(stage);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar el panel.");
        }
    }

    //ajusta la pantalla
    private void ajustarPantalla(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
    }


    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Inicio de sesión");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void togglePasswordVisibility(ActionEvent event) {
        boolean visible = txtPasswordVisible.isVisible();

        if (visible) {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);

            txtPassword.setVisible(true);
            txtPassword.setManaged(true);

            cambiarIcono("fa-eye");

        } else {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);

            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);

            cambiarIcono("fa-eye-slash");
        }
    }

    private void cambiarIcono(String iconLiteral) {
        if (togglePasswordBtn.getGraphic() instanceof FontIcon icon) {
            icon.setIconLiteral(iconLiteral);
        }
    }


    @FXML
    private void onHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/Home.fxml"));

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Encuestas");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

            // Cerrar ventana actual
            Stage currentStage = (Stage) btnReturn.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            System.err.println("Error al cargar la vista Home: " + e.getMessage());
        }
    }
}