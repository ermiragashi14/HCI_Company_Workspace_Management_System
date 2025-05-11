package controller.Admin;

import dto.UserProfileDTO;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import utils.Navigator;
import utils.TranslationManager;

import java.io.File;
import java.util.ResourceBundle;

public class AdminNavbarController {

    @FXML private Button dashboardButton;
    @FXML private Button userManagementButton;
    @FXML private Button officeManagementButton;
    @FXML private Button reservationsButton;
    @FXML private Button reportsButton;
    @FXML private ImageView navbarProfileImage;

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
        dashboardButton.setText("🏠 " + bundle.getString("admin.nav.dashboard"));
        userManagementButton.setText("👥 " + bundle.getString("admin.nav.users"));
        officeManagementButton.setText("🏢 " + bundle.getString("admin.nav.office"));
        reservationsButton.setText("📅 " + bundle.getString("admin.nav.reservations"));
        reportsButton.setText("📊 " + bundle.getString("admin.nav.reports"));
    }

    @FXML
    public void goToDashboard(ActionEvent event) {
        Navigator.navigateTo("admin_dashboard.fxml", (Node) event.getSource());
    }

    @FXML
    public void goToUserManagement(ActionEvent event) {
        Navigator.navigateTo("manage_users.fxml", (Node) event.getSource());
    }

    @FXML
    public void goToOfficeManagement(ActionEvent event) {
        Navigator.navigateTo("workspace_management.fxml", (Node) event.getSource());
    }

    @FXML
    public void goToReservations(ActionEvent event) {
        Navigator.navigateTo("reservation_management.fxml", (Node) event.getSource());
    }

    @FXML
    public void goToReports(ActionEvent event) {
        Navigator.navigateTo("reports_analytics.fxml", (Node) event.getSource());
    }
}
