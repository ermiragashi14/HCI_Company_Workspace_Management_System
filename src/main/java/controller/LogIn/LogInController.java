package controller.LogIn;

import dto.LogIn.LoginRequestDTO;
import dto.LoginResultDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import service.AuthService;
import service.SessionManager;
import utils.Navigator;
import utils.TranslationManager;
import utils.TranslationUtils;

import java.sql.SQLException;
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
    @FXML private Button helpButton;

    private final AuthService authService = new AuthService();
    ResourceBundle bundle;

    @FXML
    private void initialize() {

        TranslationUtils.setupLanguageSelector(languageSelector);
        updateLanguage();
        TranslationManager.addListener(this::updateLanguage);
    }

    private void updateLanguage() {

        bundle = TranslationManager.getBundle();
        titleText.setText(bundle.getString("app.title"));
        welcomeText.setText(bundle.getString("login.welcome"));
        yourEmailLabel.setText(bundle.getString("login.email"));
        yourPasswordLabel.setText(bundle.getString("login.password"));
        loginButton.setText(bundle.getString("login.button.submit"));
        forgotYourPasswordLink.setText(bundle.getString("login.button.forgot"));
        register.setText(bundle.getString("login.button.register"));
        languageSelector.setPromptText(bundle.getString("login.label.language"));
        helpButton.setText(bundle.getString("help.button.help"));

    }

    @FXML
    private void login() {

        bundle = TranslationManager.getBundle();
        String email = EmailField.getText().trim();
        String password = PasswordField.getText().trim();

        try {
            LoginRequestDTO loginDTO = new LoginRequestDTO(email, password);
            LoginResultDTO result = authService.loginUser(loginDTO);

            SessionManager.getInstance().setLoggedInUser(result.getUserId(), result.getRole(), result.getCompanyId());

            switch (result.getRole()) {
                case "SUPER_ADMIN" -> Navigator.navigateTo("superadmin_dashboard.fxml", loginButton);
                case "ADMIN" -> Navigator.navigateTo("admin_dashboard.fxml", loginButton);
                case "STAFF" -> Navigator.navigateTo("staff_dashboard.fxml", loginButton);
                default -> showAlert(Alert.AlertType.ERROR, bundle.getString("error.title"), bundle.getString("error.unknown_role"));
            }

        } catch (IllegalArgumentException ex) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("error.title"), bundle.getString(ex.getMessage()));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("error.database"), bundle.getString("error.login_failed") + e.getMessage());
        }
    }

    @FXML
    private void openHelp() {
        Navigator.openHelpWindow("login");
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
