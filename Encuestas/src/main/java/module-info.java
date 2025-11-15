module mx.edu.utez.encuestas {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires org.kordamp.ikonli.javafx; // Nuevo nombre del módulo para Ikonli
    requires org.kordamp.bootstrapfx.core;

    requires org.controlsfx.controls;
    requires com.oracle.database.jdbc;

    opens mx.edu.utez.encuestas.model to javafx.base;

    exports mx.edu.utez.encuestas.controller;
    // Exporta el paquete raíz para que otras clases puedan usarse
    exports mx.edu.utez.encuestas;

    // Abre el paquete raíz para que javafx.fxml pueda acceder a las clases si usas FXML ahí
    opens mx.edu.utez.encuestas to javafx.fxml;

    // Abre el paquete controller para que javafx.fxml pueda acceder a los controladores
    opens mx.edu.utez.encuestas.controller to javafx.fxml;


    opens mx.edu.utez.encuestas.views to javafx.fxml;

    exports mx.edu.utez.encuestas.model;
    exports mx.edu.utez.encuestas.config;
    exports mx.edu.utez.encuestas.dao;
    exports mx.edu.utez.encuestas.dao.impl;

}