package controller;

import dto.CreateUserDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import service.CreateUserService;
import service.SessionManager;
import utils.Navigator;

public class CreateUserDialogController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button goBackButton;

    private final CreateUserService createUserService = new CreateUserService();
    String role = SessionManager.getInstance().getLoggedInUserRole();
    int companyId = SessionManager.getInstance().getLoggedInCompanyId();

    @FXML
    public void initialize() {

        if ("SUPER_ADMIN".equals(role)) {
            roleComboBox.getItems().addAll("ADMIN", "STAFF");
        } else if ("ADMIN".equals(role)) {
            roleComboBox.getItems().add("STAFF");
        }
        roleComboBox.getSelectionModel().selectFirst();
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

            showAlert(Alert.AlertType.INFORMATION, "User Created", "The user was created successfully.");
            goBackToManageUsers();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
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
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not return to Manage Users.");
        }
    }

}
