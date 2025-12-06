package mx.edu.utez.encuestas.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import mx.edu.utez.encuestas.dao.IUsuario;
import mx.edu.utez.encuestas.dao.impl.UsuarioDaoImpl;
import mx.edu.utez.encuestas.model.Rol;
import mx.edu.utez.encuestas.model.Usuario;
import org.kordamp.ikonli.javafx.FontIcon;

public class RegistroController {

    @FXML private TextField correoField;
    @FXML private TextField nombreField;
    @FXML private TextField usuarioField;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private Button togglePasswordBtn;
    @FXML private Button btnRegistrar;

    private final IUsuario usuarioDao = new UsuarioDaoImpl();


    @FXML
    private void onRegistrar() {
        String correo = correoField.getText();
        String nombre = nombreField.getText();
        String usuario = usuarioField.getText();
        String clave = txtPassword.isVisible()
                ? txtPassword.getText()
                : txtPasswordVisible.getText();


        if (correo.isEmpty() || nombre.isEmpty() || usuario.isEmpty() || clave.isEmpty()) {
            mostrarAlerta("Todos los campos son obligatorios.");
            return;
        }

        if (!esCorreoValido(correo)) {
            mostrarAlerta("El correo no tiene un formato válido.");
            return;
        }

        if (!esUsuarioValido(usuario)) {
            mostrarAlerta("El usuario solo puede contener letras, números y guiones bajos.");
            return;
        }

        if (!esNombreValido(nombre)) {
            mostrarAlerta("El nombre solo puede contener letras y espacios.");
            return;
        }

        if (!esClaveValida(clave)) {
            mostrarAlerta("La contraseña debe tener una mayúscula, una minúscula, un número, un caracter especial y mínimo 8 caracteres");
            return;
        }

        if (usuarioDao.existeCorreo(correo)) {
            mostrarAlerta("El correo ya está registrado.");
            return;
        }

        if (usuarioDao.existeUsuario(usuario)) {
            mostrarAlerta("El nombre de usuario ya está registrado.");
            return;
        }

        Rol rolDocente = new Rol();
        rolDocente.setId(2);
        rolDocente.setNombre("docente");

        Usuario nuevo = new Usuario(correo, nombre, usuario, clave);
        nuevo.setRol(rolDocente);
        if (usuarioDao.registrarUsuario(nuevo)) {
            mostrarAlerta("Docente registrado exitosamente");
        } else {
            mostrarAlerta("Error al registrar. Intenta más tarde.");
        }
        Stage currentStage = (Stage) btnRegistrar.getScene().getWindow();
        currentStage.close();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private boolean esCorreoValido(String correo) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return correo.matches(regex);
    }

    private boolean esUsuarioValido(String usuario) {
        // solo permite letras, numero y guiones bajos
        String regex = "^[A-Za-z0-9_]+$";
        return usuario.matches(regex);
    }

    private boolean esNombreValido(String nombre) {
        // solo letras y espacios
        String regex = "^[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$";
        return nombre.matches(regex);
    }

    private boolean esClaveValida(String clave) {
        // mayúscula, una minúscula, un número, un caracter especial y mínimo 8 caracteres
        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&.#_-])[A-Za-z\\d@$!%*?&.#_-]{8,}$";
        return clave.matches(regex);
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
}