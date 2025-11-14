package mx.edu.utez.encuestas.controller;

import javafx.scene.control.Button;
import javafx.scene.layout.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

    public class FormController {

        @FXML private VBox editorContainer;
        @FXML private VBox floatingToolbarContent;

        private List<VBox> questionCards = new ArrayList<>();
        @FXML
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
            GridPane.setConstraints(optionsContainer, 0, 1, 2, 1);

            Separator separator = new Separator();
            separator.setStyle("-fx-padding: 10px 0;");

            HBox bottomBar = createBottomBar(newCard);

            newCard.getChildren().addAll(grid, optionsContainer, separator, bottomBar);

            editorContainer.getChildren().add(newCard);
            questionCards.add(newCard);
        }

        private VBox createOptionsVBox(String type) {
            VBox optionsVBox = new VBox(10);

            addOptionRow(optionsVBox, type);

            HBox addOptionRow = new HBox(10);
            addOptionRow.getStyleClass().add("add-option-row");

            Control placeholder = type.equals("Checkboxes") ? new CheckBox() : new RadioButton();
            placeholder.setVisible(false);

            Label addLabel = new Label("Agregar opción");
            addLabel.setStyle("-fx-text-fill: #4285F4; -fx-font-weight: bold; -fx-cursor: hand;");

            addOptionRow.getChildren().addAll(placeholder, addLabel);

            addLabel.setOnMouseClicked(e -> addOptionRow(optionsVBox, type));

            optionsVBox.getChildren().add(addOptionRow);
            return optionsVBox;
        }

        private void addOptionRow(VBox optionsVBox, String type) {
            HBox optionRow = new HBox(10);
            optionRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            Control inputControl = type.equals("Checkboxes") ? new CheckBox() : new RadioButton();
            TextField optionText = new TextField();
            optionText.setPromptText("Opción " + optionsVBox.getChildren().size());
            HBox.setHgrow(optionText, Priority.ALWAYS);

            Button deleteButton = new Button("x");
            deleteButton.getStyleClass().add("icon-button");

            deleteButton.setOnAction(e -> optionsVBox.getChildren().remove(optionRow));

            optionRow.getChildren().addAll(inputControl, optionText, deleteButton);

            optionsVBox.getChildren().add(optionsVBox.getChildren().size() - 1, optionRow);
        }

        private HBox createBottomBar(VBox cardToDelete) {
            HBox bottomBar = new HBox(15);
            bottomBar.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

            Button deleteButton = new Button("🗑️");
            deleteButton.getStyleClass().add("icon-button");
            deleteButton.setOnAction(e -> {
                editorContainer.getChildren().remove(cardToDelete);
                questionCards.remove(cardToDelete);
            });

            Button duplicateButton = new Button("📄");
            duplicateButton.getStyleClass().add("icon-button");

            Separator separator = new Separator(javafx.geometry.Orientation.VERTICAL);
            separator.setPrefHeight(20.0);

            Label requiredLabel = new Label("Required");
            ToggleButton requiredToggle = new ToggleButton();
            requiredToggle.getStyleClass().add("toggle-button");
            // Lógica de requerido: podrías actualizar un modelo de datos aquí.

            bottomBar.getChildren().addAll(deleteButton, duplicateButton, separator, requiredLabel, requiredToggle);
            return bottomBar;
        }


    }