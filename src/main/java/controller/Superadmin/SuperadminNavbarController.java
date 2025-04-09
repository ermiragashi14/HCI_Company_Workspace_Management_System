package controller.Superadmin;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import utils.Navigator;
import utils.TranslationManager;

import java.util.ResourceBundle;



public class SuperadminNavbarController {

    @FXML
    private Button dashboardButton;
    @FXML
    private Button reservationsButton;
    @FXML
    private Button workspacesButton;
    @FXML
    private Button auditLogButton;
    @FXML
    private Button userCreationButton;

    @FXML
    public void initialize() {
        applyTranslations();

        TranslationManager.addListener(this::applyTranslations);
    }
    private void applyTranslations() {
        ResourceBundle bundle = TranslationManager.getBundle();

        dashboardButton.setText("🏠 " + bundle.getString("super.nav.dashboard"));
        reservationsButton.setText("📅 " + bundle.getString("super.nav.reservations"));
        workspacesButton.setText("🏢 " + bundle.getString("super.nav.workspaces"));
        auditLogButton.setText("🧾 " + bundle.getString("super.nav.auditlog"));
        userCreationButton.setText("👤 " + bundle.getString("super.nav.createuser"));
    }

    @FXML
    private void goToDashboard(ActionEvent event) {
        Navigator.navigateTo("superadmin_dashboard.fxml", (Node) event.getSource());
    }

    @FXML
    private void goToReservations(ActionEvent event) {
        Navigator.navigateTo("superadmin_reservations.fxml", (Node) event.getSource());
    }

    @FXML
    private void goToWorkspaces(ActionEvent event) {
        Navigator.navigateTo("superadmin_workspaces.fxml", (Node) event.getSource());
    }

    @FXML
    private void goToAuditLog(ActionEvent event) {
        Navigator.navigateTo("audit_log.fxml", (Node) event.getSource());
    }

    @FXML
    private void goToUserCreation(ActionEvent event) {
        Navigator.navigateTo("create_users.fxml", (Node) event.getSource());
    }
}
