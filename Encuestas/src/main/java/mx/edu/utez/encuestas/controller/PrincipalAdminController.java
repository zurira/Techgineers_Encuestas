package mx.edu.utez.encuestas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.edu.utez.encuestas.dao.impl.UsuarioDaoImpl;
import mx.edu.utez.encuestas.model.Usuario;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PrincipalAdminController implements Initializable {

    @FXML private TableView<Usuario> tableViewDocentes;
    @FXML private TableColumn<Usuario, Integer> colNo;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colCorreo;
    @FXML private TableColumn<Usuario, String> colUsuario;

    @FXML private TextField buscarField;
    @FXML private Label lblSinResultados;
    @FXML private Button btnAgregarDocente;
    @FXML private Button logoutButton;
    @FXML private Label nombreAdmin;

    private final UsuarioDaoImpl usuarioDao = new UsuarioDaoImpl();
    private ObservableList<Usuario> listaDocentes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //columna de No. para mejor visualización
        colNo.setCellValueFactory(cellData -> {
            int index = tableViewDocentes.getItems().indexOf(cellData.getValue()) + 1;
            return new javafx.beans.property.SimpleIntegerProperty(index).asObject();
        });

        // configuración de columnas
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));

        cargarDocentes();

        // listener para el buscador
        buscarField.textProperty().addListener((obs, oldVal, newVal) -> filtrarDocentes(newVal));
    }

    private void cargarDocentes() {
        try {
            List<Usuario> docentes = usuarioDao.findAll();
            listaDocentes = FXCollections.observableArrayList(docentes);
            tableViewDocentes.setItems(listaDocentes);
            lblSinResultados.setVisible(docentes.isEmpty());
        } catch (SQLException e) {
            System.err.println("Error al cargar docentes: " + e.getMessage());
        }
    }

    private void filtrarDocentes(String texto) {
        if (texto == null || texto.isBlank()) {
            tableViewDocentes.setItems(listaDocentes);
            lblSinResultados.setVisible(listaDocentes.isEmpty());
        } else {
            List<Usuario> filtrados = listaDocentes.stream()
                    .filter(u -> u.getNombre().toLowerCase().contains(texto.toLowerCase())
                            || u.getCorreo().toLowerCase().contains(texto.toLowerCase())
                            || u.getNombreUsuario().toLowerCase().contains(texto.toLowerCase()))
                    .collect(Collectors.toList());

            tableViewDocentes.setItems(FXCollections.observableArrayList(filtrados));
            lblSinResultados.setVisible(filtrados.isEmpty());
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
    private void onAgregarDocente(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/registro.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Registro de docente");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar la vista de registro: " + e.getMessage());
        }

    }

    @FXML
    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/modalcerrars.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Cerrar sesión");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar la vista " + e.getMessage());
        }
    }
}