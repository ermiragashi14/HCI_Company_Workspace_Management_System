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
<<<<<<< HEAD
=======
    opens views to javafx.fxml, javafx.graphics;
>>>>>>> 0c1f48a318ff05ce132cdd7eb18c4fa671cdc449

    // ✅ Export required packages
    exports controller;
    exports model;
    exports repository;
    exports service;
    exports utils;
    exports app;
    opens app to javafx.fxml, javafx.graphics;
}
