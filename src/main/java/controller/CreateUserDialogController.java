package controller;

import dto.CreateUserDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import service.AuditLogService;
import service.CreateUserService;
import service.SessionManager;
import utils.KeyboardNavigator;
import utils.Navigator;
import utils.TranslationManager;
import java.util.ResourceBundle;

public class CreateUserDialogController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button goBackButton;
    @FXML private Label fullnameLabel;
    @FXML private Label emailLabel;
    @FXML private Label roleLabel;
    @FXML private Label passwordLabel;
    @FXML private Label confirmPasswordLabel;
    @FXML private Label phoneLabel;
    @FXML private Button createButton;
    @FXML private Text createNewUserText;
    @FXML private AnchorPane createUser;

    private final CreateUserService createUserService = new CreateUserService();
    String role = SessionManager.getInstance().getLoggedInUserRole();
    int companyId = SessionManager.getInstance().getLoggedInCompanyId();
    ResourceBundle bundle;
    private final AuditLogService auditlog=new AuditLogService();

    @FXML
    public void initialize() {

        updateLanguage();
        TranslationManager.addListener(this::updateLanguage);

        if ("SUPER_ADMIN".equals(role)) {
            roleComboBox.getItems().addAll("ADMIN", "STAFF");
        } else if ("ADMIN".equals(role)) {
            roleComboBox.getItems().add("STAFF");
        }
        roleComboBox.getSelectionModel().selectFirst();
        KeyboardNavigator.enableNavigation(createUser);
    }

    private void updateLanguage(){

        bundle = TranslationManager.getBundle();
        goBackButton.setText(bundle.getString("create.user.dialog.goback"));
        fullnameLabel.setText(bundle.getString("manage.users.name"));
        emailLabel.setText(bundle.getString("manage.users.email"));
        phoneLabel.setText(bundle.getString("manage.users.phone"));
        passwordLabel.setText(bundle.getString("register.password"));
        confirmPasswordLabel.setText(bundle.getString("register.confirmPassword"));
        roleLabel.setText(bundle.getString("manage.users.role"));
        createButton.setText(bundle.getString("create.user.dialog.button"));
        createNewUserText.setText(bundle.getString("create.user.dialog.text"));
    }

    @FXML
    private void onCreateClicked() {

        try {
            CreateUserDTO dto = new CreateUserDTO(
                    fullNameField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim(),
                    passwordField.getText(),
                    confirmPasswordField.getText(),
                    roleComboBox.getValue()
            );

            createUserService.createUser(role, companyId,dto);
            String details=dto.getRole()+" user named "+dto.getFullName()+" was created!";
            auditlog.log("CREATE", details);
            showAlert(Alert.AlertType.INFORMATION, bundle.getString("create.user.dialog.userCreated"), bundle.getString("create.user.dialog.success"));
            goBackToManageUsers();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("error.title"), e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void goBackToManageUsers() {

        try {
            Navigator.navigateTo("manage_users.fxml", goBackButton);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, bundle.getString("create.user.dialog.navigationError"), bundle.getString("create.user.dialog.navigationMessage"));
        }
    }

}
