package controller.Staff;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.scene.control.Button;
import service.UserProfileService;
import utils.Navigator;
import utils.TranslationManager;

import java.io.File;
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

        File defaultAvatar = new File("user_photos/default_avatar.png");
        if (defaultAvatar.exists()) {
            navbarProfileImage.setImage(new Image(defaultAvatar.toURI().toString()));
        } else {
            System.err.println("Default avatar image not found at 'user_photos/default.png'");
        }
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
        Navigator.navigateTo("new_reservation.fxml", (Node) event.getSource());
    }

    @FXML
    private void goToMyReservations(ActionEvent event) {
        Navigator.navigateTo("my_reservations.fxml", (Node) event.getSource());
    }
}
