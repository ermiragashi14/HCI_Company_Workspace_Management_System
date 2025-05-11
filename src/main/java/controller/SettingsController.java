package controller;

import dto.UserProfileDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import service.SessionManager;
import service.UserProfileService;
import utils.Navigator;
import utils.TranslationManager;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

public class SettingsController {

    @FXML private Label fullNameLabel;
    @FXML private Label emailLabel;
    @FXML private TextField phoneField;
    @FXML private ImageView profileImage;
    @FXML private Label companyNameLabel;
    @FXML private Label companyEmailLabel;
    @FXML private Button disableAccountButton;
    @FXML private Button changePasswordButton;
    @FXML private Button goBackButton;

    @FXML private Button removePhotoButton;
    @FXML private Button updatePhoneButton;
    @FXML private Button changePhotoButton;

    @FXML private Label emailTextLabel;
    @FXML private Label phoneNumberTextLabel;
    @FXML private Label companyNameTextLabel;
    @FXML private Label companyEmailTextLabel;
    @FXML private Label fullNameTextLabel;
    @FXML private TitledPane companyInfoPane;

    private final UserProfileService userSettingsService = new UserProfileService();
    private ResourceBundle bundle;

        @FXML
        public void initialize() {

            bundle = TranslationManager.getBundle();
            TranslationManager.addListener(this::updateLanguage);
            updateLanguage();

            int userId = SessionManager.getInstance().getLoggedInUserId();
            UserProfileDTO profile = userSettingsService.getUserProfile(userId);
            if (profile == null) return;

            fullNameLabel.setText(profile.getFullName());
            emailLabel.setText(profile.getEmail());
            phoneField.setText(profile.getPhoneNumber());

            File avatarFile;
            if (profile.getAvatarPath() != null && new File(profile.getAvatarPath()).exists()) {
                avatarFile = new File(profile.getAvatarPath());
            } else {
                avatarFile = new File("user_photos/default.png");
            }

            profileImage.setImage(new Image(avatarFile.toURI().toString()));

            companyNameLabel.setText(profile.getCompanyName());
            companyEmailLabel.setText(profile.getCompanyEmail());
        }

    private void updateLanguage() {
        bundle = TranslationManager.getBundle();

        changePasswordButton.setText(bundle.getString("settings.changePassword"));
        disableAccountButton.setText(bundle.getString("settings.disableAccount"));
        goBackButton.setText(bundle.getString("common.back"));
        removePhotoButton.setText(bundle.getString("settings.removePhoto"));
        updatePhoneButton.setText(bundle.getString("settings.updatePhone"));
        changePhotoButton.setText(bundle.getString("settings.changePhoto"));

        fullNameTextLabel.setText(bundle.getString("settings.fullName"));
        emailTextLabel.setText(bundle.getString("settings.email"));
        phoneNumberTextLabel.setText(bundle.getString("settings.phone"));
        companyNameTextLabel.setText(bundle.getString("settings.companyName"));
        companyEmailTextLabel.setText(bundle.getString("settings.companyEmail"));
        companyInfoPane.setText(bundle.getString("settings.titled.companyInfo"));

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

        File defaultAvatar = new File("user_photos/default_avatar.png");
        if (defaultAvatar.exists()) {
            profileImage.setImage(new Image(defaultAvatar.toURI().toString()));
        } else {
            System.err.println("Default avatar not found in file system.");
        }

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

    @FXML
    private void onGoBackClicked() {
        String role = SessionManager.getInstance().getLoggedInUserRole();

        String fxml = switch (role.toUpperCase()) {
            case "SUPER_ADMIN" -> "superadmin_dashboard.fxml";
            case "ADMIN" -> "admin_dashboard.fxml";
            case "STAFF" -> "staff_dashboard.fxml";
            default -> null;
        };

        if (fxml != null) {
            Navigator.navigateTo(fxml, goBackButton);
        } else {
            System.err.println("Unknown role or no dashboard assigned.");
        }
    }

}