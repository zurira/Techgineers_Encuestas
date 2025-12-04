package mx.edu.utez.encuestas.controller;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.edu.utez.encuestas.dao.impl.EncuestaImpl;
import mx.edu.utez.encuestas.dao.IEncuesta;
import mx.edu.utez.encuestas.dao.impl.OpcionDaoImpl;
import mx.edu.utez.encuestas.dao.impl.PreguntaDaoImpl;
import mx.edu.utez.encuestas.model.Encuesta;
import mx.edu.utez.encuestas.model.Pregunta;
import org.controlsfx.control.action.Action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML private TilePane encuestasContainer;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private Button loginButton;
    @FXML private TextField searchField;

    private final IEncuesta encuestaDao = new EncuestaImpl();
    private static final String OPCION_TODAS = "Todas las Categorías";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configuración de la cuadrícula
        encuestasContainer.setPadding(new Insets(20));
        encuestasContainer.setHgap(30);
        encuestasContainer.setVgap(40);

        loadCategories();
        loadEncuestas(null);

        //listener para buscador
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterBySearch(newVal);
        });
    }

    private void loadCategories() {
        try {
            List<String> categories = encuestaDao.findAllActiveCategories();
            categories.add(0, OPCION_TODAS);

            categoryFilter.setItems(FXCollections.observableArrayList(categories));
            categoryFilter.getSelectionModel().select(OPCION_TODAS);

            // Listener que llama a la función de filtrado al cambiar la categoría
            categoryFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    filterByCategoria(newVal);
                }
            });

        } catch (Exception e) {
            System.err.println("Error al cargar categorías: " + e.getMessage());
        }
    }

    private void filterByCategoria(String categoria) {
        if (OPCION_TODAS.equals(categoria)) {
            loadEncuestas(null);
        } else {
            loadEncuestas(categoria);
        }
    }

    private void filterBySearch(String texto) {
        new Thread(() -> {
            try {
                List<Encuesta> encuestas = encuestaDao.findAllActive();

                List<Encuesta> filtradas = encuestas.stream()
                        .filter(e -> e.getTitulo().toLowerCase().contains(texto.toLowerCase())
                                || e.getDescripcionCorta().toLowerCase().contains(texto.toLowerCase()))
                        .toList();

                Platform.runLater(() -> {
                    encuestasContainer.getChildren().clear();
                    if (filtradas.isEmpty()) {
                        encuestasContainer.getChildren().add(new Label("No hay encuestas que coincidan con la búsqueda."));
                    } else {
                        filtradas.forEach(this::createSurveyCard);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    Label error = new Label("Error al filtrar encuestas: " + e.getMessage());
                    encuestasContainer.getChildren().add(error);
                });
            }
        }).start();
    }

    private void loadEncuestas(String categoria) {
        new Thread(() -> {
            try {
                List<Encuesta> encuestas;
                if (categoria == null || categoria.equals(OPCION_TODAS)) {
                    encuestas = encuestaDao.findAllActive();
                } else {
                    encuestas = encuestaDao.findActiveByCategory(categoria);
                }

                Platform.runLater(() -> {
                    encuestasContainer.getChildren().clear();
                    if (encuestas.isEmpty()) {
                        encuestasContainer.getChildren().add(new Label("No hay encuestas activas en esta categoría."));
                    } else {
                        encuestas.forEach(this::createSurveyCard);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    Label error = new Label("Error al cargar encuestas: " + e.getMessage());
                    encuestasContainer.getChildren().add(error);
                });
            }
        }).start();
    }

    private void createSurveyCard(Encuesta encuesta) {
        VBox card = new VBox(5);
        card.getStyleClass().add("survey-card");
        card.setPrefWidth(200);
        card.setAlignment(Pos.TOP_LEFT);

        // Contenedor de Imagen
        VBox imageWrapper = new VBox();
        imageWrapper.getStyleClass().add("image-wrapper");
        imageWrapper.setPrefSize(200, 150);
        imageWrapper.setAlignment(Pos.CENTER);

        if (encuesta.getImagen() != null) {
            Image image = new Image(new ByteArrayInputStream(encuesta.getImagen()));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(250);
            imageView.setFitHeight(250);
            imageView.setPreserveRatio(true);
            imageWrapper.getChildren().add(imageView);
        }

        // Título
        Label titleLabel = new Label(encuesta.getTitulo());
        titleLabel.getStyleClass().add("card-title");
        titleLabel.setWrapText(true);

        // Descripción
        Label descriptionLabel = new Label(encuesta.getDescripcionCorta());
        descriptionLabel.getStyleClass().add("card-description");
        descriptionLabel.setWrapText(true);

        card.getChildren().addAll(imageWrapper, titleLabel, descriptionLabel);

        card.setOnMouseClicked(e -> abrirModalEncuesta(encuesta));

        encuestasContainer.getChildren().add(card);
    }

    @FXML
    private void onLogin(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Inicio de sesión");
            stage.setScene(new Scene(root));
            stage.show();
            stage.setMaximized(true);

            //cerrar ventana
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            System.err.println("Error al cargar la vista de registro: " + e.getMessage());
        }

    }

    private void abrirModalEncuesta(Encuesta encuesta) {
        try {
            PreguntaDaoImpl preguntaDao = new PreguntaDaoImpl();
            OpcionDaoImpl opcionDao = new OpcionDaoImpl();

            // carga preguntas
            List<Pregunta> preguntas = preguntaDao.obtenerPreguntasPorEncuesta(encuesta.getId());

            // carg opciones
            for (Pregunta pregunta : preguntas) {
                pregunta.setOpciones(opcionDao.obtenerOpcionesPorPregunta(pregunta.getId()));
            }

            encuesta.setPreguntas(preguntas);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mx/edu/utez/encuestas/views/encuestaModal.fxml"));
            Parent root = loader.load();

            EncuestaModalController controller = loader.getController();
            controller.setEncuesta(encuesta);

            Stage stage = new Stage();
            stage.setTitle("Responder Encuesta");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException ex) {
            System.err.println("Error al abrir modal de encuesta: " + ex.getMessage());
        }
    }

}
