package controller;

import dto.ManagedUserDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import service.ManageUsersService;
import service.SessionManager;
import utils.Navigator;

import java.util.List;

public class ManageUsersController {

    @FXML private TextField nameFilterField;
    @FXML private TextField emailFilterField;
    @FXML private TextField createdAtField;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private ComboBox<String> roleFilterCombo;
    @FXML private Button searchButton;
    @FXML private Button createUserButton;
    @FXML private TableView<ManagedUserDTO> userTable;
    @FXML private TableColumn<ManagedUserDTO, String> nameColumn;
    @FXML private TableColumn<ManagedUserDTO, String> emailColumn;
    @FXML private TableColumn<ManagedUserDTO, String> phoneColumn;
    @FXML private TableColumn<ManagedUserDTO, String> roleColumn;
    @FXML private TableColumn<ManagedUserDTO, String> statusColumn;
    @FXML private TableColumn<ManagedUserDTO, String> createdAtColumn;
    @FXML private TableColumn<ManagedUserDTO, Void> actionColumn;

    private final ManageUsersService userService = new ManageUsersService();

    @FXML
    private void initialize() {
        setupFilterList();
        setupComboBoxes();
        setupTableColumns();
        loadAllUsers();
    }

    private void setupFilterList() {
        ObservableList<String> filters = FXCollections.observableArrayList("Name", "Email", "Status", "Role", "Created At");
    }

    private void setupComboBoxes() {
        statusFilterCombo.setItems(FXCollections.observableArrayList("ALL", "ACTIVE", "DISABLED"));
        statusFilterCombo.getSelectionModel().selectFirst();

        roleFilterCombo.setItems(FXCollections.observableArrayList("ALL", "SUPER_ADMIN", "ADMIN", "STAFF"));
        roleFilterCombo.getSelectionModel().selectFirst();
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        addDisableButtonToTable();
    }

    @FXML
    private void onSearchClicked() {

        String name = nameFilterField.getText().trim();
        String email = emailFilterField.getText().trim();
        String createdAt = createdAtField.getText().trim();

        if (name.isEmpty()) name = null;
        if (email.isEmpty()) email = null;
        if (createdAt.isEmpty()) createdAt = null;

        String status = statusFilterCombo.getValue();
        if ("ALL".equalsIgnoreCase(status)) status = null;

        String role = roleFilterCombo.getValue();
        if ("ALL".equalsIgnoreCase(role)) role = null;

        try {
            List<ManagedUserDTO> users = userService.filterUsers(name, email, status, role, createdAt);
            userTable.setItems(FXCollections.observableArrayList(users));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Search Error", "An error occurred while searching for users.");
        }
    }

    @FXML
    private void onRefreshClicked() {
        nameFilterField.clear();
        emailFilterField.clear();
        createdAtField.clear();
        statusFilterCombo.getSelectionModel().selectFirst();
        roleFilterCombo.getSelectionModel().selectFirst();
        loadAllUsers();
    }


    @FXML
    private void onCreateUserClicked() {
        Navigator.navigateTo("create_user_dialog.fxml", createUserButton);
    }

    private void addDisableButtonToTable() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button();

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                ManagedUserDTO user = getTableView().getItems().get(getIndex());
                String currentStatus = user.getStatus();
                btn.setText(currentStatus.equalsIgnoreCase("ACTIVE") ? "Disable" : "Enable");

                btn.setOnAction(event -> {
                    if (user.getId() == SessionManager.getInstance().getLoggedInUserId()) {
                        showAlert(Alert.AlertType.WARNING, "Action Denied", "You cannot change your own status.");
                        return;
                    }

                    String targetStatus = currentStatus.equalsIgnoreCase("ACTIVE") ? "DISABLED" : "ACTIVE";
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Change user status to " + targetStatus + "?", ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            boolean success = userService.updateUserStatus(user.getId(), targetStatus);
                            if (success) {
                                onSearchClicked();
                            } else {
                                showAlert(Alert.AlertType.ERROR, "Update Failed", "Could not update user status.");
                            }
                        }
                    });
                });

                setGraphic(btn);
            }
        });
    }


    private void loadAllUsers() {
        try {
            List<ManagedUserDTO> users = userService.filterUsers(null, null, "ALL", "ALL", null);
            userTable.setItems(FXCollections.observableArrayList(users));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Load Error", "An error occurred while loading users.");
        }
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}