package controller.Register;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import model.Company;
import model.User;
import service.SessionManager;
import service.UserService;
import utils.Navigator;
import utils.TranslationUtils;
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

    private final UserService userService = new UserService();

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

    @FXML
    private void register() {
        try {
            Company company = new Company(
                    0,
                    companyNameField.getText().trim(),
                    companyEmailField.getText().trim(),
                    companyPhoneField.getText().trim(),
                    null
            );

            String password = superadminPasswordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();

            User superAdmin = new User(
                    0,
                    "SUPER_ADMIN",
                    null,
                    null,
                    superadminNameField.getText().trim(),
                    superadminEmailField.getText().trim(),
                    superadminPhoneField.getText().trim(),
                    "ACTIVE"
            );

            int userId = userService.registerSuperAdminAndCompany(company, superAdmin, password, confirmPassword);
            SessionManager.getInstance().setLoggedInUser(userId, "SUPER_ADMIN", company.getId());

            showAlert(Alert.AlertType.INFORMATION, "success.title", "success.registration_successful");
            Navigator.navigateTo("superadmin_dashboard.fxml", registerButton);

        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.ERROR, "error.title", ex.getMessage());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "error.database", "error.databaseRegister");
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "error.unexpected", "error.unexpectedWhileRegistering");
        }
    }


    @FXML
    private void backToLogin() {
        if (backToLoginLink == null) {
            System.err.println("Error: backToLoginLink is null!");
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
}
