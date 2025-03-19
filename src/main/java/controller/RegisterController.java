package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Company;
import model.User;
import repository.CompanyRepository;
import repository.UserRepository;
import service.PasswordHasher;
import service.SessionManager;
import utils.Navigator;

import java.sql.SQLException;

public class RegisterController {

    @FXML private TextField companyNameField, companyEmailField, companyPhoneField, superadminNameField, superadminEmailField, superadminPhoneField;
    @FXML private PasswordField superadminPasswordField;
    @FXML private Button registerButton;

    private final CompanyRepository companyRepository = new CompanyRepository();
    private final UserRepository userRepository = new UserRepository();

    @FXML
    private void register() {
        try {

            String companyName = companyNameField.getText();
            String companyEmail = companyEmailField.getText();
            String companyPhone = companyPhoneField.getText();

            if (companyName.isEmpty() || companyEmail.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Company fields cannot be empty.");
                return;
            }

            int companyId = companyRepository.registerCompany(new Company(0, companyName, companyEmail, companyPhone, null));

            String adminName = superadminNameField.getText();
            String adminEmail = superadminEmailField.getText();
            String adminPhone = superadminPhoneField.getText();
            String adminPassword = superadminPasswordField.getText();

            if (adminName.isEmpty() || adminEmail.isEmpty() || adminPassword.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Admin fields cannot be empty.");
                return;
            }

            String salt = PasswordHasher.generateSalt();
            String hashedPassword = PasswordHasher.generateSaltedHash(adminPassword, salt);

            int superAdminId = userRepository.registerSuperAdmin(new User(companyId, "SUPER_ADMIN", hashedPassword, salt, adminName, adminEmail, adminPhone, "ACTIVE"));

            SessionManager.getInstance().setLoggedInUser(superAdminId, "SUPER_ADMIN");

            showAlert(Alert.AlertType.INFORMATION, "Success", "Company and Super Admin registered successfully!");
            Navigator.navigateTo("admin_dashboard.fxml", registerButton);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Registration failed: " + e.getMessage());
        }
    }

    @FXML
    private void backToLogin() {
        Navigator.navigateTo("login.fxml", registerButton);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
