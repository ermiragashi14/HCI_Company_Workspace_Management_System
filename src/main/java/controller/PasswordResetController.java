package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import repository.UserRepository;
import service.OTPService;
import service.PasswordHasher;
import utils.Navigator;
import utils.ValidationUtils;
import java.sql.SQLException;

public class PasswordResetController {

    @FXML private TextField emailField;
    @FXML private TextField otpField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button sendOtpButton;
    @FXML private Button submitButton;
    @FXML private Hyperlink goBackToLoginLink;

    private final UserRepository userRepository = new UserRepository();

    @FXML
    private void sendOtp() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter your email.");
            return;
        }
        try {

            boolean userExists = userRepository.getUserByEmail(email).isPresent();
            if (!userExists) {
                showAlert(Alert.AlertType.ERROR, "Error", "This email is not registered.");
                return;
            }

            boolean otpSent = OTPService.sendOtpToUser(email);
            if (!otpSent) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to send OTP. Please try again later.");
                return;
            }
            showAlert(Alert.AlertType.INFORMATION, "OTP Sent", "An OTP has been sent to your email.");

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error checking email: " + e.getMessage());
        }
    }

    @FXML
    private void handleResetPassword() {
        String email = emailField.getText().trim();
        String otp = otpField.getText().trim();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (email.isEmpty() || otp.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled.");
            return;
        }

        if (!ValidationUtils.doPasswordsMatch(newPassword, confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match.");
            return;
        }

        try {
            boolean isOtpValid = OTPService.verifyOTP(email, otp);
            if (!isOtpValid) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid or expired OTP.");
                return;
            }

            String salt = PasswordHasher.generateSalt();
            String hashedPassword = PasswordHasher.generateSaltedHash(newPassword, salt);

            boolean passwordUpdated = userRepository.updateUserPassword(email, hashedPassword, salt);
            if (!passwordUpdated) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update password.");
                return;
            }

            OTPService.removeOTP(email);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Password has been reset successfully. You can now log in.");
            Navigator.navigateTo("login.fxml", submitButton);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Password reset failed: " + e.getMessage());
        }
    }

    @FXML
    private void goBackToLogin() {
        Navigator.navigateTo("login.fxml", goBackToLoginLink);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
