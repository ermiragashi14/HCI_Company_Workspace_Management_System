package controller.PasswordReset;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import service.PasswordResetService;
import utils.Navigator;
import utils.TranslationUtils;
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

    private final PasswordResetService resetService = new PasswordResetService();

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

    @FXML
    private void sendOtp() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "error.title", "error.empty_fields");
            return;
        }

        try {
            resetService.sendOtp(email);
            showAlert(Alert.AlertType.INFORMATION, "success.title", "success.otp_sent");
        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.ERROR, "error.title", ex.getMessage());
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "error.database", "error.otp_send_failed");
        }
    }

    @FXML
    private void handleResetPassword() {
        String email = emailField.getText().trim();
        String otp = otpField.getText().trim();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (email.isEmpty() || otp.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "error.title", "error.empty_fields");
            return;
        }

        try {
            resetService.resetPassword(email, otp, newPassword, confirmPassword);
            showAlert(Alert.AlertType.INFORMATION, "success.title", "success.password_reset");
            Navigator.navigateTo("login.fxml", submitButton);
        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.ERROR, "error.title", ex.getMessage());
        } catch (Exception e) {
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
