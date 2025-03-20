module com.example.hci_company_workspace_management_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires mysql.connector.j;
    requires jakarta.mail;

    // ✅ FIX: Allow JavaFX to access `RegisterController`
    opens controller to javafx.fxml;

    // ✅ Ensure main package is accessible

    // ✅ Export required packages
    exports controller;
    exports model;
    exports repository;
    exports service;
    exports utils;
    exports app;
    opens app to javafx.fxml, javafx.graphics;
}
