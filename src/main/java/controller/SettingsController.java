package controller;

import dto.UserProfileDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import service.SessionManager;
import service.UserProfileService;
import utils.ImageUtils;
import utils.Navigator;

import java.io.File;
import java.io.IOException;

public class SettingsController {

    @FXML private Label fullNameLabel;
    @FXML private Label emailLabel;
    @FXML private TextField phoneField;
    @FXML private ImageView profileImage;
    @FXML private Label companyNameLabel;
    @FXML private Label companyEmailLabel;
    @FXML private Button disableAccountButton;
    @FXML private Button changePasswordButton;

    private final UserProfileService userSettingsService = new UserProfileService();

    @FXML
    public void initialize() {
        int userId = SessionManager.getInstance().getLoggedInUserId();
        UserProfileDTO profile = userSettingsService.getUserProfile(userId);
        if (profile == null) return;

        fullNameLabel.setText(profile.getFullName());
        emailLabel.setText(profile.getEmail());
        phoneField.setText(profile.getPhoneNumber());

        profileImage.setImage(ImageUtils.loadProfileImage(profile.getAvatarPath()));

        companyNameLabel.setText(profile.getCompanyName());
        companyEmailLabel.setText(profile.getCompanyEmail());
    }

    @FXML
    private void updatePhone() {
        String newPhone = phoneField.getText().trim();
        if (!newPhone.isEmpty()) {
            userSettingsService.updatePhone(SessionManager.getInstance().getLoggedInUserId(), newPhone);
        }
    }

    @FXML
    private void onChangePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                userSettingsService.updateAvatar(SessionManager.getInstance().getLoggedInUserId(), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            profileImage.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void onRemovePhoto() {
        int userId = SessionManager.getInstance().getLoggedInUserId();
        userSettingsService.removeAvatar(userId);
        profileImage.setImage(new Image(new File("user_photos/default.png").toURI().toString()));
    }

    @FXML
    private void changePassword() {
        Navigator.navigateTo("password_reset.fxml", changePasswordButton);
    }

    @FXML
    private void disableAccount() {
        userSettingsService.disableUser(SessionManager.getInstance().getLoggedInUserId());
        SessionManager.getInstance().clearSession();
        Navigator.navigateTo("login.fxml", disableAccountButton);
    }
}