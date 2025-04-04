package controller.Register;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import model.Company;
import model.User;
import repository.CompanyRepository;
import repository.UserRepository;
import service.PasswordHasher;
import service.SessionManager;
import utils.Navigator;
import utils.TranslationUtils;
import utils.ValidationUtils;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class RegisterController {

    @FXML private TextField companyNameField, companyEmailField, companyPhoneField;
    @FXML private TextField superadminNameField, superadminEmailField, superadminPhoneField;
    @FXML private PasswordField superadminPasswordField, confirmPasswordField;
    @FXML private Button registerButton;
    @FXML private Hyperlink backToLoginLink;
    @FXML private ComboBox<String> languageSelector;
    @FXML private Text titleText;
    @FXML private Label companyNameLabel, companyEmailLabel, companyPhoneLabel;
    @FXML private Label superadminNameLabel, superadminEmailLabel, superadminPhoneLabel;
    @FXML private Label passwordLabel, confirmPasswordLabel;

    @FXML
    private void initialize() {
        TranslationUtils.setupLanguageSelector(languageSelector, this::updateLanguage);
        updateLanguage();
    }

    private void updateLanguage() {
        ResourceBundle bundle = TranslationUtils.getBundle();

        titleText.setText(bundle.getString("register.title"));
        companyNameLabel.setText(bundle.getString("register.companyName"));
        companyEmailLabel.setText(bundle.getString("register.companyEmail"));
        companyPhoneLabel.setText(bundle.getString("register.companyPhone"));
        superadminNameLabel.setText(bundle.getString("register.superadminName"));
        superadminEmailLabel.setText(bundle.getString("register.superadminEmail"));
        superadminPhoneLabel.setText(bundle.getString("register.superadminPhone"));
        passwordLabel.setText(bundle.getString("register.password"));
        confirmPasswordLabel.setText(bundle.getString("register.confirmPassword"));
        registerButton.setText(bundle.getString("register.button.submit"));
        backToLoginLink.setText(bundle.getString("register.button.back"));
        languageSelector.setPromptText(bundle.getString("register.label.language"));
    }


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
            String adminConfirmPassword = confirmPasswordField.getText().trim();

            if (!validateSuperAdmin(adminName, adminEmail, adminPhone, adminPassword, adminConfirmPassword, companyEmail)) return;
            int companyId = companyRepository.registerCompany(new Company(0, companyName, companyEmail, companyPhone, null));

            String salt = PasswordHasher.generateSalt();
            String hashedPassword = PasswordHasher.generateSaltedHash(adminPassword, salt);

            int superAdminId = userRepository.registerUser(new User(companyId, "SUPER_ADMIN", hashedPassword, salt, adminName, adminEmail, adminPhone, "ACTIVE"));

            SessionManager.getInstance().setLoggedInUser(superAdminId, "SUPER_ADMIN", companyId);

            showAlert(Alert.AlertType.INFORMATION, "success.title", "success.registration_successful");

            Navigator.navigateTo("superadmin_dashboard.fxml", registerButton);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "error.database", "error.databaseRegister");
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "error.unexpected", "error.unexpectedWhileRegistering");
        }
    }

    @FXML
    private void backToLogin() {
        if (backToLoginLink == null) {
            System.err.println("⚠ Error: backToLoginLink is null!");
            return;
        }

        Navigator.navigateTo("login.fxml", backToLoginLink);
    }


    private void showAlert(Alert.AlertType alertType, String titleKey, String messageKey) {
        ResourceBundle bundle = TranslationUtils.getBundle();
        Alert alert = new Alert(alertType);
        alert.setTitle(bundle.getString(titleKey));
        alert.setContentText(bundle.getString(messageKey));
        alert.showAndWait();
    }

    private boolean validateCompany(String name, String email, String phone) throws SQLException {
        if (name.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "error.title", "error.empty_fields");
            return false;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "error.title", "error.invalid_email");
            return false;
        }

        if (!ValidationUtils.isValidPhoneNumber(phone)) {
            showAlert(Alert.AlertType.ERROR, "error.title", "error.invalid_phone");
            return false;
        }

        if (companyRepository.companyExists(name, email)) {
            showAlert(Alert.AlertType.ERROR, "error.title", "error.company_exists");
            return false;
        }

        String companyDomain = ValidationUtils.getEmailDomain(email);
        if (companyRepository.companyDomainExists(companyDomain)) {
            showAlert(Alert.AlertType.ERROR, "error.title", "error.company_domain_exists");
            return false;
        }

        return true;
    }


    private boolean validateSuperAdmin(String name, String email, String phone, String password, String confirmPassword,String companyEmail) throws SQLException {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()|| confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "error.title", "error.empty_superadmin_fields");
            return false;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "error.title", "error.invalid_superadmin_email");
            return false;
        }

        if (!email.endsWith(ValidationUtils.getEmailDomain(companyEmail))) {
            showAlert(Alert.AlertType.ERROR, "error.title", "error.invalid_superadmin_domain");
            return false;
        }

        if (!ValidationUtils.isValidPhoneNumber(phone)) {
            showAlert(Alert.AlertType.ERROR, "error.title", "error.invalid_phone");
            return false;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            showAlert(Alert.AlertType.ERROR, "error.title", "error.weak_password");
            return false;
        }

        if (!ValidationUtils.doPasswordsMatch(password, confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "error.title", "error.passwords_no_match");
            return false;
        }

        if (userRepository.userExists(email)) {
            showAlert(Alert.AlertType.ERROR, "error.title", "error.user_exists");
            return false;
        }
        return true;
    }
}
