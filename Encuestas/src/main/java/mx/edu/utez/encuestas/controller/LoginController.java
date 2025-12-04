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
        String clave = txtPassword.getText();

        if (usuario.isEmpty() || clave.isEmpty()) {
            mostrarAlerta("Los campos de usuario y contraseña no pueden estar vacíos");
            return;
        }

        // valida usuario en la base de datos
        Usuario usuarioValido = usuarioDao.validarLogin(usuario, clave);

        if (usuarioValido != null) {
            System.out.println("Inicio de sesión exitoso");

            try {
                FXMLLoader loader;
                switch (usuarioValido.getRol().getNombre().trim().toLowerCase()) {
                    case "administrador":
                        System.out.println("Cargando vista admin");
                        loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/dashboardAdmin.fxml"));
                        break;

                    case "docente":
                        System.out.println("Cargando vista de docente");
                        loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/dashboardDocente.fxml"));
                        break;

                    default:
                        mostrarAlerta("Rol no reconocido");
                        return;
                }

                Region root = loader.load();

                // obtiene controlador
                Object controller = loader.getController();

                //se pasa el id de acuerdo al controllador
                if (controller instanceof PrincipalDocenteController docenteController) {
                    docenteController.setUsuarioActivo(usuarioValido);
                } else if (controller instanceof PrincipalAdminController adminController) {
                    adminController.setUsuarioActivo(usuarioValido);
                } else if (controller instanceof DashboardDocenteController dashboardController) {
                    dashboardController.setUsuarioActivo(usuarioValido);
                }else if (controller instanceof DashboardAdminController dashboardController) {
                    dashboardController.setUsuarioActivo(usuarioValido);
                }

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setMaximized(true);
                stage.setScene(scene);
                stage.show();

                // ajusta pantalla
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
    private void togglePasswordVisibility(ActionEvent event) {
        boolean isVisible = txtPasswordVisible.isVisible();

        if (isVisible) {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);

            if (togglePasswordBtn.getGraphic() instanceof FontIcon icon) {
                icon.setIconLiteral("fa-eye");
            }
        } else {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);

            if (togglePasswordBtn.getGraphic() instanceof FontIcon icon) {
                icon.setIconLiteral("fa-eye-slash");
            }
        }
    }

    @FXML
    private void onHome(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/Home.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Encuestas");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setMaximized(true);

            //cerrar ventana
            Stage currentStage = (Stage) btnReturn.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            System.err.println("Error al cargar la vista de registro: " + e.getMessage());
        }

    }
}