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
import utils.TranslationManager;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

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
    @FXML private Label filtersLabel;
    @FXML private Button refreshButton;
    @FXML private Label nameFilterLabel;
    @FXML private Label emailFilterLabel;
    @FXML private Label statusFilterLabel;
    @FXML private Label createdAtFilterLabel;
    @FXML private Label roleFilterLabel;

    private final ManageUsersService userService = new ManageUsersService();
    ResourceBundle bundle = TranslationManager.getBundle();

    @FXML
    private void initialize() {
        setupFilterList();
        setupComboBoxes();
        setupTableColumns();
        loadAllUsers();
        applyTranslations();
        TranslationManager.addListener(this::applyTranslations);
    }

    private void applyTranslations() {

        createUserButton.setText(bundle.getString("manage.users.create"));
        searchButton.setText(bundle.getString("manage.users.search"));
        filtersLabel.setText(bundle.getString("manage.users.filters"));
        refreshButton.setText(bundle.getString("manage.users.refresh"));
        nameFilterLabel.setText(bundle.getString("manage.users.name"));
        emailFilterLabel.setText(bundle.getString("manage.users.email"));
        statusFilterLabel.setText(bundle.getString("manage.users.status"));
        roleFilterLabel.setText(bundle.getString("manage.users.role"));
        createdAtFilterLabel.setText(bundle.getString("manage.users.createdAt"));

        statusColumn.setText(bundle.getString("manage.users.status"));
        roleColumn.setText(bundle.getString("manage.users.role"));
        phoneColumn.setText(bundle.getString("manage.users.phone"));
        emailColumn.setText(bundle.getString("manage.users.email"));
        nameColumn.setText(bundle.getString("manage.users.fullName"));
        createdAtColumn.setText(bundle.getString("manage.users.createdAt"));
        actionColumn.setText(bundle.getString("manage.users.action"));
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

            showAlert(Alert.AlertType.ERROR, "", bundle.getString("manage.users.alert.searchError"));

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
                btn.setText("ACTIVE".equalsIgnoreCase(currentStatus) ? "Disable" : "Enable");

                btn.setOnAction(event -> {
                    if (user.getId() == SessionManager.getInstance().getLoggedInUserId()) {
                        showAlert(Alert.AlertType.WARNING, "", bundle.getString("manage.users.alert.selfDisable"));
                        return;
                    }

                    String targetStatus = currentStatus.equalsIgnoreCase("ACTIVE") ? "DISABLED" : "ACTIVE";
                    String msg = MessageFormat.format(bundle.getString("manage.users.alert.confirm"), targetStatus);
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);

                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            boolean success = userService.updateUserStatus(user.getId(), targetStatus);
                            if (success) {
                                onSearchClicked();
                            } else {
                                showAlert(Alert.AlertType.ERROR, "", bundle.getString("manage.users.alert.failed"));
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
            showAlert(Alert.AlertType.ERROR, "", bundle.getString("manage.users.alert.loadError"));
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