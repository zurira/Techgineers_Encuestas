module mx.edu.utez.encuestas {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires org.kordamp.ikonli.javafx; // Nuevo nombre del módulo para Ikonli
    requires org.kordamp.bootstrapfx.core;

    requires org.controlsfx.controls;

    opens mx.edu.utez.encuestas to javafx.fxml;
    exports mx.edu.utez.encuestas;


}