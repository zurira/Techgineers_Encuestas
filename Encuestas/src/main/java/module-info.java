module mx.edu.utez.encuestas {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens mx.edu.utez.encuestas to javafx.fxml;
    opens mx.edu.utez.encuestas.views to javafx.fxml;
    opens mx.edu.utez.encuestas.css to javafx.fxml;
    opens mx.edu.utez.encuestas.img to javafx.fxml;


    exports mx.edu.utez.encuestas;


}