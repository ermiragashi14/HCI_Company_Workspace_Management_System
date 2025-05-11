package controller.Register;

import dto.CompanyDTO;
import dto.Register.RegisterRequestDTO;
import dto.Register.RegisterResultDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import service.SessionManager;
import service.RegisterService;
import utils.Navigator;
import utils.TranslationManager;
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

    private final RegisterService registerService = new RegisterService();
    ResourceBundle bundle;

    @FXML
    private void initialize() {

        TranslationUtils.setupLanguageSelector(languageSelector);
        updateLanguage();
        TranslationManager.addListener(this::updateLanguage);
    }

    private void updateLanguage() {

        bundle = TranslationManager.getBundle();
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

            CompanyDTO companyDTO = new CompanyDTO(
                    companyNameField.getText().trim(),
                    companyEmailField.getText().trim(),
                    companyPhoneField.getText().trim()
            );

            RegisterRequestDTO userDTO = new RegisterRequestDTO(
                    superadminNameField.getText().trim(),
                    superadminEmailField.getText().trim(),
                    superadminPhoneField.getText().trim(),
                    superadminPasswordField.getText().trim(),
                    confirmPasswordField.getText().trim()
            );

            if (hasEmptyFields(companyDTO, userDTO)) {
                showAlert(Alert.AlertType.WARNING, "error.title", "error.empty_fields");
                return;
            }

        try {
            RegisterResultDTO result = registerService.registerSuperAdminAndCompany(companyDTO, userDTO);
            SessionManager.getInstance().setLoggedInUser(result.getUserId(), "SUPER_ADMIN", result.getCompanyId());

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

    private boolean hasEmptyFields(CompanyDTO company, RegisterRequestDTO user) {

        return company.getName().isEmpty() ||
                company.getEmail().isEmpty() ||
                company.getPhoneNumber().isEmpty() ||
                user.getFullName().isEmpty() ||
                user.getEmail().isEmpty() ||
                user.getPhoneNumber().isEmpty() ||
                user.getPassword().isEmpty() ||
                user.getConfirmPassword().isEmpty();
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

        bundle = TranslationManager.getBundle();
        Alert alert = new Alert(alertType);
        alert.setTitle(bundle.getString(titleKey));
        alert.setContentText(bundle.getString(messageKey));
        alert.showAndWait();
    }
}
