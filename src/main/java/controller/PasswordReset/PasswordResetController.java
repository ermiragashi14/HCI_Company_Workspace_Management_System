package controller.PasswordReset;

import dto.PasswordReset.PasswordResetDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import service.AuditLogService;
import service.PasswordResetService;
import service.SessionManager;
import utils.KeyboardNavigator;
import utils.Navigator;
import utils.TranslationManager;
import utils.TranslationUtils;
import java.util.ResourceBundle;

public class PasswordResetController {

    @FXML private TextField emailField;
    @FXML private TextField otpField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button sendOtpButton;
    @FXML private Button submitButton;
    @FXML private Hyperlink goBackLink;
    @FXML private ComboBox<String> languageSelector;
    @FXML private Text titleText;
    @FXML private Label emailLabel, otpLabel, newPasswordLabel, confirmPasswordLabel;
    @FXML private AnchorPane passwordRootPane;

    private final PasswordResetService resetService = new PasswordResetService();
    ResourceBundle bundle;
    private final AuditLogService auditlog= new AuditLogService();

    @FXML
    private void initialize() {

        TranslationUtils.setupLanguageSelector(languageSelector);
        updateLanguage();
        TranslationManager.addListener(this::updateLanguage);
        KeyboardNavigator.enableNavigation(passwordRootPane);
    }

    private void updateLanguage() {

        bundle = TranslationManager.getBundle();
        titleText.setText(bundle.getString("passwordreset.title"));
        emailLabel.setText(bundle.getString("passwordreset.email"));
        otpLabel.setText(bundle.getString("passwordreset.otp"));
        newPasswordLabel.setText(bundle.getString("passwordreset.newPassword"));
        confirmPasswordLabel.setText(bundle.getString("passwordreset.confirmPassword"));
        sendOtpButton.setText(bundle.getString("passwordreset.sendOtp"));
        submitButton.setText(bundle.getString("passwordreset.submit"));
        goBackLink.setText(bundle.getString("passwordreset.goBack"));
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
            auditlog.log("INFO", "An OTP code was sent to this user!");
            showAlert(Alert.AlertType.INFORMATION, "success.title", "success.otp_sent");
        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.ERROR, "error.title", ex.getMessage());
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "error.database", "error.otp_send_failed");
        }
    }

    @FXML
    private void handleResetPassword() {

        PasswordResetDTO dto = new PasswordResetDTO(
                emailField.getText().trim(),
                otpField.getText().trim(),
                newPasswordField.getText(),
                confirmPasswordField.getText()
        );


        if (dto.getEmail().isEmpty() || dto.getOtp().isEmpty()
                || dto.getNewPassword().isEmpty() || dto.getConfirmPassword().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "error.title", "error.empty_fields");
            return;
        }

        try {
            resetService.resetPassword(dto);
            String details="This user changed their password!";
            auditlog.log("UPDATE",details);
            showAlert(Alert.AlertType.INFORMATION, "success.title", "success.password_reset");
            Navigator.navigateTo("login.fxml", submitButton);
        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.ERROR, "error.title", ex.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "error.database", "error.password_reset_failed");
        }
    }

    @FXML
    private void openHelp() {
        Navigator.openHelpWindow("forgetpassword");
    }

    @FXML
    private void goBack() {
        if (SessionManager.getInstance().getLoggedInUserId() > 0) {
            Navigator.navigateTo("settings.fxml", goBackLink);
        } else {
            Navigator.navigateTo("login.fxml", goBackLink);
        }
    }

    private void showAlert(Alert.AlertType alertType, String titleKey, String messageKey) {

        bundle = TranslationManager.getBundle();
        Alert alert = new Alert(alertType);
        alert.setTitle(bundle.getString(titleKey));
        alert.setContentText(bundle.getString(messageKey));
        alert.showAndWait();
    }
}
