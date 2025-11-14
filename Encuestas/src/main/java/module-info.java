module mx.edu.utez.encuestas {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens mx.edu.utez.encuestas to javafx.fxml;
    exports mx.edu.utez.encuestas;

    exports mx.edu.utez.encuestas.controller;
    opens mx.edu.utez.encuestas.controller to javafx.fxml;
}