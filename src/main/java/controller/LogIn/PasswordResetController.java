package controller.LogIn;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import repository.UserRepository;
import service.OTPService;
import service.PasswordHasher;
import utils.Navigator;
import utils.TranslationUtils;
import utils.ValidationUtils;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class PasswordResetController {

    @FXML private TextField emailField;
    @FXML private TextField otpField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button sendOtpButton;
    @FXML private Button submitButton;
    @FXML private Hyperlink goBackToLoginLink;
    @FXML private ComboBox<String> languageSelector;
    @FXML private Text titleText;
    @FXML private Label emailLabel, otpLabel, newPasswordLabel, confirmPasswordLabel;


    @FXML
    private void initialize() {
        TranslationUtils.setupLanguageSelector(languageSelector, this::updateLanguage);
        updateLanguage();
    }


    private void updateLanguage() {
        ResourceBundle bundle = TranslationUtils.getBundle();

        titleText.setText(bundle.getString("passwordreset.title"));
        emailLabel.setText(bundle.getString("passwordreset.email"));
        otpLabel.setText(bundle.getString("passwordreset.otp"));
        newPasswordLabel.setText(bundle.getString("passwordreset.newPassword"));
        confirmPasswordLabel.setText(bundle.getString("passwordreset.confirmPassword"));
        sendOtpButton.setText(bundle.getString("passwordreset.sendOtp"));
        submitButton.setText(bundle.getString("passwordreset.submit"));
        goBackToLoginLink.setText(bundle.getString("passwordreset.backToLogin"));
        languageSelector.setPromptText(bundle.getString("passwordreset.language"));
    }


    private final UserRepository userRepository = new UserRepository();

    @FXML
    private void sendOtp() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "error.title", "error.enter_email");
            return;
        }
        try {

            boolean userExists = userRepository.getUserByEmail(email).isPresent();
            if (!userExists) {
                showAlert(Alert.AlertType.ERROR, "error.title", "error.email_not_registered");
                return;
            }

            boolean otpSent = OTPService.sendOtpToUser(email);
            if (!otpSent) {
                showAlert(Alert.AlertType.ERROR, "error.title", "error.otp_send_failed");
                return;
            }
            showAlert(Alert.AlertType.INFORMATION, "success.title", "success.otp_sent");

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "error.database", "error.email_check_failed");
        }
    }

    @FXML
    private void handleResetPassword() {
        String email = emailField.getText().trim();
        String otp = otpField.getText().trim();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (email.isEmpty() || otp.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "error.title", "error.empty_fields");
            return;
        }

        if (!ValidationUtils.doPasswordsMatch(newPassword, confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "error.title", "error.passwords_no_match");
            return;
        }

        try {
            boolean isOtpValid = OTPService.verifyOTP(email, otp);
            if (!isOtpValid) {
                showAlert(Alert.AlertType.ERROR, "error.title", "error.invalid_otp");
                return;
            }

            String salt = PasswordHasher.generateSalt();
            String hashedPassword = PasswordHasher.generateSaltedHash(newPassword, salt);

            boolean passwordUpdated = userRepository.updateUserPassword(email, hashedPassword, salt);
            if (!passwordUpdated) {
                showAlert(Alert.AlertType.ERROR, "error.title", "error.password_update_failed");
                return;
            }

            OTPService.removeOTP(email);
            showAlert(Alert.AlertType.INFORMATION, "success.title", "success.password_reset");
            Navigator.navigateTo("login.fxml", submitButton);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "error.database", "error.password_reset_failed");

        }
    }

    @FXML
    private void goBackToLogin() {
        Navigator.navigateTo("login.fxml", goBackToLoginLink);
    }

    private void showAlert(Alert.AlertType alertType, String titleKey, String messageKey) {
        ResourceBundle bundle = TranslationUtils.getBundle();
        Alert alert = new Alert(alertType);
        alert.setTitle(bundle.getString(titleKey));
        alert.setContentText(bundle.getString(messageKey));
        alert.showAndWait();
    }
}
