package controller.Admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import utils.TranslationManager;

import java.io.IOException;
import java.util.ResourceBundle;

public class AdminNavbarController {

    @FXML private Button dashboardButton;
    @FXML private Button userManagementButton;
    @FXML private Button officeManagementButton;
    @FXML private Button reservationsButton;
    @FXML private Button reportsButton;

    @FXML
    public void initialize() {
        translate();
        TranslationManager.addListener(this::translate);
    }

    private void translate() {
        ResourceBundle bundle = TranslationManager.getBundle();

        dashboardButton.setText("🏠 " + bundle.getString("admin.nav.dashboard"));
        userManagementButton.setText("👥 " + bundle.getString("admin.nav.users"));
        officeManagementButton.setText("🏢 " + bundle.getString("admin.nav.office"));
        reservationsButton.setText("📅 " + bundle.getString("admin.nav.reservations"));
        reportsButton.setText("📊 " + bundle.getString("admin.nav.reports"));
    }

    private void switchScene(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/" + fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToDashboard(ActionEvent event) {
        switchScene(event, "admin_dashboard.fxml");
    }

    @FXML
    public void goToUserManagement(ActionEvent event) {
        switchScene(event, "manage_users.fxml");
    }

    @FXML
    public void goToOfficeManagement(ActionEvent event) {
        switchScene(event, "workspace_management.fxml");
    }

    @FXML
    public void goToReservations(ActionEvent event) {
        switchScene(event, "reservation_management.fxml");
    }

    @FXML
    public void goToReports(ActionEvent event) {
        switchScene(event, "reports.fxml");
    }
}
