package controller.Superadmin;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import service.UserProfileService;
import utils.Navigator;
import utils.TranslationManager;

import java.io.File;
import java.util.ResourceBundle;

public class SuperadminNavbarController {

    @FXML private Button dashboardButton;
    @FXML private Button reservationsButton;
    @FXML private Button workspacesButton;
    @FXML private Button auditLogButton;
    @FXML private Button manageUsersButton;
    @FXML private ImageView navbarProfileImage;

    private final UserProfileService userSettingsService = new UserProfileService();
    ResourceBundle bundle;

    @FXML
    public void initialize() {

        updateLanguage();
        TranslationManager.addListener(this::updateLanguage);

        File defaultAvatar = new File("user_photos/default_avatar.png");
        if (defaultAvatar.exists()) {
            navbarProfileImage.setImage(new Image(defaultAvatar.toURI().toString()));
        } else {
            System.err.println("Default avatar image not found at 'user_photos/default.png'");
        }

    }

    private void updateLanguage() {

        bundle = TranslationManager.getBundle();
        dashboardButton.setText("🏠 " + bundle.getString("super.nav.dashboard"));
        reservationsButton.setText("📅 " + bundle.getString("super.nav.reservations"));
        workspacesButton.setText("🏢 " + bundle.getString("super.nav.workspaces"));
        auditLogButton.setText("🧾 " + bundle.getString("super.nav.auditlog"));
        manageUsersButton.setText("👤 " + bundle.getString("super.nav.createuser"));
    }

    @FXML
    private void goToDashboard(ActionEvent event) {
        Navigator.navigateTo("superadmin_dashboard.fxml", (Node) event.getSource());
    }

    @FXML
    private void goToReservations(ActionEvent event) {
        Navigator.navigateTo("reservation_management.fxml", (Node) event.getSource());
    }

    @FXML
    private void goToWorkspaces(ActionEvent event) {
        Navigator.navigateTo("workspace_management.fxml", (Node) event.getSource());
    }

    @FXML
    private void goToAuditLog(ActionEvent event) {
        Navigator.navigateTo("audit_log.fxml", (Node) event.getSource());
    }

    @FXML
    private void goToManageUsers(ActionEvent event) {
        Navigator.navigateTo("manage_users.fxml", (Node) event.getSource());
    }
}
