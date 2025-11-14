package mx.edu.utez.encuestas.controller;

import javafx.scene.control.Button;
import javafx.scene.layout.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

    public class FormController {

        @FXML private VBox editorContainer; // Contenedor central donde se apilan las tarjetas de preguntas
        @FXML private VBox floatingToolbarContent; // Contenedor interno de los botones de la barra flotante

        private List<VBox> questionCards = new ArrayList<>(); // Lista para llevar el seguimiento de las tarjetas

        @
        public void initialize() {

            if (!floatingToolbarContent.getChildren().isEmpty()) {
                Button addButton = (Button) floatingToolbarContent.getChildren().get(0);
                addButton.setOnAction(event -> addNewQuestionCard());
            }

        }


        private void addNewQuestionCard() {
            VBox newCard = new VBox();
            newCard.getStyleClass().add("question-card");
            newCard.setPadding(new javafx.geometry.Insets(20, 20, 10, 20));
            newCard.setSpacing(10); // Espaciado entre elementos de la tarjeta

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            TextField questionTitle = new TextField();
            questionTitle.setPromptText("Pregunta sin título");
            questionTitle.getStyleClass().add("question-title-field");
            GridPane.setConstraints(questionTitle, 0, 0);
            GridPane.setHgrow(questionTitle, Priority.ALWAYS);

            ComboBox<String> typeSelector = new ComboBox<>();
            typeSelector.getItems().addAll("Multiple choice", "Short answer", "Checkboxes");
            typeSelector.setValue("Multiple choice");
            GridPane.setConstraints(typeSelector, 1, 0);

            grid.getChildren().addAll(questionTitle, typeSelector);

            VBox optionsContainer = createOptionsVBox(typeSelector.getValue());
            GridPane.setConstraints(optionsContainer, 0, 1, 2, 1); // Span de 2 columnas

            Separator separator = new Separator();
            separator.setStyle("-fx-padding: 10px 0;");

            HBox bottomBar = createBottomBar(newCard); // Pasa la tarjeta para la lógica de eliminación

            newCard.getChildren().addAll(grid, optionsContainer, separator, bottomBar);

            editorContainer.getChildren().add(newCard);
            questionCards.add(newCard);
        }


    }