module com.example.hci_company_workspace_management_system {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires mysql.connector.j;
    requires jakarta.mail;

    opens controller to javafx.fxml;

    exports controller;
    exports model;
    exports repository;
    exports service;
    exports utils;
    exports app;
    opens app to javafx.fxml, javafx.graphics;
    exports controller.Admin;
    opens controller.Admin to javafx.fxml;
    exports controller.Register;
    opens controller.Register to javafx.fxml;
    exports controller.LogIn;
    opens controller.LogIn to javafx.fxml;
    exports controller.Superadmin to javafx.fxml;
    opens controller.Superadmin to javafx.fxml;
}
