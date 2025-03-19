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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterController {

    @FXML private TextField companyNameField, companyEmailField, companyPhoneField;
    @FXML private TextField superadminNameField, superadminEmailField, superadminPhoneField;
    @FXML private PasswordField superadminPasswordField;
    @FXML private Button registerButton;

    private final CompanyRepository companyRepository = new CompanyRepository();
    private final UserRepository userRepository = new UserRepository();

    @FXML
    private void register() {
        try {

            String companyName = companyNameField.getText().trim();
            String companyEmail = companyEmailField.getText().trim();
            String companyPhone = companyPhoneField.getText().trim();

            if (!validateCompany(companyName, companyEmail, companyPhone)) return;

            String adminName = superadminNameField.getText().trim();
            String adminEmail = superadminEmailField.getText().trim();
            String adminPhone = superadminPhoneField.getText().trim();
            String adminPassword = superadminPasswordField.getText().trim();

            if (!validateSuperAdmin(adminName, adminEmail, adminPhone, adminPassword, companyEmail)) return;

            int companyId = companyRepository.registerCompany(new Company(0, companyName, companyEmail, companyPhone, null));

            String salt = PasswordHasher.generateSalt();
            String hashedPassword = PasswordHasher.generateSaltedHash(adminPassword, salt);

            int superAdminId = userRepository.registerSuperAdmin(
                    new User(companyId, "SUPER_ADMIN", hashedPassword, salt, adminName, adminEmail, adminPhone, "ACTIVE")
            );

            SessionManager.getInstance().setLoggedInUser(superAdminId, "SUPER_ADMIN");

            showAlert(Alert.AlertType.INFORMATION, "Success", "Company and Super Admin registered successfully!");

            Navigator.navigateTo("superadmin_dashboard.fxml", registerButton);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Registration failed: " + e.getMessage());
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Unexpected Error", "An unexpected error occurred: " + ex.getMessage());
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

    private boolean validateCompany(String name, String email, String phone) throws SQLException {
        if (name.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Company fields cannot be empty.");
            return false;
        }

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email", "Please enter a valid company email.");
            return false;
        }

        if (!isValidPhoneNumber(phone)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Phone Number", "Please enter a valid phone number.");
            return false;
        }

        if (companyRepository.companyExists(name, email)) {
            showAlert(Alert.AlertType.ERROR, "Error", "A company with this name or email is already registered.");
            return false;
        }

        String companyDomain = getEmailDomain(email);
        if (companyRepository.companyDomainExists(companyDomain)) {
            showAlert(Alert.AlertType.ERROR, "Error", "A company with this email domain already exists.");
            return false;
        }

        return true;
    }


    private boolean validateSuperAdmin(String name, String email, String phone, String password, String companyEmail) throws SQLException {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Super Admin fields cannot be empty.");
            return false;
        }

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email", "Please enter a valid Super Admin email.");
            return false;
        }

        if (!email.endsWith(getEmailDomain(companyEmail))) {
            showAlert(Alert.AlertType.ERROR, "Invalid Domain", "Super Admin's email must belong to the same domain as the company email.");
            return false;
        }

        if (!isValidPhoneNumber(phone)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Phone Number", "Please enter a valid phone number.");
            return false;
        }

        if (!isValidPassword(password)) {
            showAlert(Alert.AlertType.ERROR, "Weak Password", "Password must be at least 8 characters long, contain a digit, an uppercase letter, a lowercase letter, and a special character.");
            return false;
        }

        if (userRepository.superAdminExists(email)) {
            showAlert(Alert.AlertType.ERROR, "Error", "A Super Admin with this email is already registered.");
            return false;
        }
        return true;
    }


    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private String getEmailDomain(String email) {
        return email.substring(email.indexOf("@"));
    }

    private boolean isValidPhoneNumber(String phone) {
        String phoneRegex = "^\\+?[0-9]{9,15}$";
        Pattern pattern = Pattern.compile(phoneRegex);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

}
