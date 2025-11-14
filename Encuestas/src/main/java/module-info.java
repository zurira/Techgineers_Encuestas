module mx.edu.utez.encuestas {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens mx.edu.utez.encuestas to javafx.fxml;
    exports mx.edu.utez.encuestas;
}