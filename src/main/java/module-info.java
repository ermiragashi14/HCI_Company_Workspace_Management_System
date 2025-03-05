module com.example.hci_company_workspace_management_system {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.hci_company_workspace_management_system to javafx.fxml;
    exports com.example.hci_company_workspace_management_system;
}