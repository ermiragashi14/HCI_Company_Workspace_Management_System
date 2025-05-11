package controller.Staff;

import dto.UserProfileDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.scene.control.Button;
import service.SessionManager;
import service.UserProfileService;
import utils.ImageUtils;
import utils.Navigator;
import utils.TranslationManager;

import java.util.ResourceBundle;

public class StaffNavbarController {

    @FXML private Button dashboardButton;
    @FXML private Button availableWorkspacesButton;
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
        dashboardButton.setText("🏠 " + bundle.getString("staff.nav.dashboard"));
        availableWorkspacesButton.setText("🏢 " + bundle.getString("staff.nav.available"));
        reservationsButton.setText("📅 " + bundle.getString("staff.nav.reserve"));
        reportsButton.setText("📋 " + bundle.getString("staff.nav.myreservations"));
    }

    @FXML
    private void goToDashboard(ActionEvent event) {
        Navigator.navigateTo("staff_dashboard.fxml", (Node) event.getSource());
    }

    @FXML
    private void goToAvailableWorkspaces(ActionEvent event) {
        Navigator.navigateTo("available_workspaces.fxml", (Node) event.getSource());
    }

    @FXML
    private void goToMakeReservation(ActionEvent event) {
        Navigator.navigateTo("make_reservation.fxml", (Node) event.getSource());
    }

    @FXML
    private void goToMyReservations(ActionEvent event) {
        Navigator.navigateTo("my_reservations.fxml", (Node) event.getSource());
    }
}
