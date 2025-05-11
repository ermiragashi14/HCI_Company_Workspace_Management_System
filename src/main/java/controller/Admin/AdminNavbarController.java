package controller.Admin;

import dto.UserProfileDTO;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import service.SessionManager;
import service.UserProfileService;
import utils.ImageUtils;
import utils.Navigator;
import utils.TranslationManager;

import java.util.ResourceBundle;

public class AdminNavbarController {

    @FXML private Button dashboardButton;
    @FXML private Button userManagementButton;
    @FXML private Button officeManagementButton;
    @FXML private Button reservationsButton;
    @FXML private Button reportsButton;
    @FXML private ImageView navbarProfileImage;

    private final UserProfileService userSettingsService = new UserProfileService();
    ResourceBundle bundle;

    @FXML
    public void initialize() {

        updateLanguage();
        TranslationManager.addListener(this::updateLanguage);
        int userId = SessionManager.getInstance().getLoggedInUserId();
        UserProfileDTO profile = userSettingsService.getUserProfile(userId);
        navbarProfileImage.setImage(ImageUtils.loadProfileImage(profile.getAvatarPath()));
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
