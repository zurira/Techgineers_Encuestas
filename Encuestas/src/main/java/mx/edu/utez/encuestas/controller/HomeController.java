package mx.edu.utez.encuestas.controller;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import mx.edu.utez.encuestas.dao.impl.EncuestaImpl;
import mx.edu.utez.encuestas.dao.IEncuesta;
import mx.edu.utez.encuestas.model.Encuesta;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML private TilePane encuestasContainer;
    @FXML private ComboBox<String> categoryFilter;
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

        ImageView imageView = new ImageView(encuesta.getImagen());
        imageView.setFitWidth(180);
        imageView.setFitHeight(130);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("survey-image");

        imageWrapper.getChildren().add(imageView);

        // Título
        Label titleLabel = new Label(encuesta.getTitulo());
        titleLabel.getStyleClass().add("card-title");
        titleLabel.setWrapText(true);

        // Descripción
        Label descriptionLabel = new Label(encuesta.getDescripcionCorta());
        descriptionLabel.getStyleClass().add("card-description");
        descriptionLabel.setWrapText(true);

        card.getChildren().addAll(imageWrapper, titleLabel, descriptionLabel);

        card.setOnMouseClicked(e -> System.out.println("Navegar a encuesta: " + encuesta.getTitulo()));

        encuestasContainer.getChildren().add(card);
    }
}
