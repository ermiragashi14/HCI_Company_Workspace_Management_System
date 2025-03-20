package controller;

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
import java.util.Optional;
import java.util.ResourceBundle;

public class LogInController {

    @FXML private TextField EmailField;
    @FXML private PasswordField PasswordField;
    @FXML private Button loginButton;
    @FXML private Hyperlink forgotYourPasswordLink;
    @FXML private Hyperlink register;
    @FXML private ComboBox<String> languageSelector;
    @FXML private Label yourEmailLabel;
    @FXML private Label yourPasswordLabel;
    @FXML private Text welcomeText;
    @FXML private Text titleText;

    private final UserRepository userRepository = new UserRepository();
    private final CompanyRepository companyRepository = new CompanyRepository();

    @FXML
    private void initialize() {
        TranslationUtils.setupLanguageSelector(languageSelector, this::updateLanguage);
        updateLanguage();
    }

    private void updateLanguage() {
        ResourceBundle bundle = TranslationUtils.getBundle();

        titleText.setText(bundle.getString("app.title"));
        welcomeText.setText(bundle.getString("login.welcome"));
        yourEmailLabel.setText(bundle.getString("login.email"));
        yourPasswordLabel.setText(bundle.getString("login.password"));
        loginButton.setText(bundle.getString("login.button.submit"));
        forgotYourPasswordLink.setText(bundle.getString("login.button.forgot"));
        register.setText(bundle.getString("login.button.register"));
        languageSelector.setPromptText(bundle.getString("login.label.language"));
    }

    @FXML
    private void login() {

        ResourceBundle bundle = TranslationUtils.getBundle();

        String email = EmailField.getText().trim();
        String password = PasswordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("error.title"), bundle.getString("error.empty_fields"));
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("error.title"), bundle.getString("error.invalid_email"));
            return;
        }

        try {

            String domain = ValidationUtils.getEmailDomain(email);
            Optional<Company> companyOptional = companyRepository.getCompanyByDomain(domain);

            if (companyOptional.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, bundle.getString("error.title"), bundle.getString("error.no_company_domain"));
                return;
            }

            Company company = companyOptional.get();

            Optional<User> userOptional = userRepository.getUserByEmail(email);
            if (userOptional.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, bundle.getString("error.title"), bundle.getString("error.invalid_login"));
                return;
            }

            User user = userOptional.get();

            if (user.getCompanyId() != company.getId()) {
                showAlert(Alert.AlertType.ERROR, bundle.getString("error.title"), bundle.getString("error.mismatched_company"));
                return;
            }

            if (!PasswordHasher.compareSaltedHash(password, user.getSalt(), user.getPasswordHash())) {
                showAlert(Alert.AlertType.ERROR, bundle.getString("error.title"), bundle.getString("error.invalid_login"));
                return;
            }

            SessionManager.getInstance().setLoggedInUser(user.getId(), user.getRole());

            switch (user.getRole()) {
                case "SUPER_ADMIN":
                    Navigator.navigateTo("superadmin_dashboard.fxml", loginButton);
                    break;
                case "REGULAR_ADMIN":
                    Navigator.navigateTo("admin_dashboard.fxml", loginButton);
                    break;
                case "STAFF_USER":
                    Navigator.navigateTo("staff_dashboard.fxml", loginButton);
                    break;
                default:
                    showAlert(Alert.AlertType.ERROR, bundle.getString("error.title"), bundle.getString("error.unknown_role"));
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("error.database"), bundle.getString("error.login_failed") + e.getMessage());
        }
    }

    @FXML
    private void passwordReset() {
        Navigator.navigateTo("password_reset.fxml", forgotYourPasswordLink);
    }

    @FXML
    private void goToRegister() {
        Navigator.navigateTo("register.fxml", register);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
