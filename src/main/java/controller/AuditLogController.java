package controller;

import java.util.List;

import dto.AuditLogDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import model.User;
import service.AuditLogService;
import service.SessionManager;
import utils.KeyboardNavigator;
import utils.TranslationManager;

import java.time.LocalDate;
import java.util.ResourceBundle;

public class AuditLogController {

        @FXML private ComboBox<User> userFilter;
        @FXML private ComboBox<String> actionTypeFilter;
        @FXML private DatePicker startDateFilter;
        @FXML private DatePicker endDateFilter;
        @FXML private TableView<AuditLogDTO> auditTable;
        @FXML private TableColumn<AuditLogDTO, String> userFullNameColumn;
        @FXML private TableColumn<AuditLogDTO, String> actionTypeColumn;
        @FXML private TableColumn<AuditLogDTO, String> detailsColumn;
        @FXML private TableColumn<AuditLogDTO, String> timestampColumn;
        @FXML private Label companyNameLabel;
        @FXML private Button searchButton;
        @FXML private Button resetButton;
        @FXML private Button exportButton;
        @FXML private VBox navbarContainer;
        @FXML private VBox ribbonContainer;
        @FXML private VBox mainContentContainer;
        @FXML private VBox filterContainer;

        private final AuditLogService service = new AuditLogService();
        private ResourceBundle bundle;

        @FXML
        public void initialize() {
            bundle = TranslationManager.getBundle();

            updateLanguage();
            TranslationManager.addListener(this::updateLanguage);

            try {
                int companyId = SessionManager.getInstance().getLoggedInCompanyId();
                companyNameLabel.setText(service.getCompanyName(companyId));
                setupUserFilter(service.getUsersByCompany(companyId));
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "", bundle.getString("auditLogs.alert.loadCompanyError"));
            }

            actionTypeFilter.setItems(FXCollections.observableArrayList("INFO", "UPDATE", "DELETE", "CREATE"));
            setColumnFactories();
            loadLogs(null, null, null, null);

            KeyboardNavigator.enableNavigation(
                    navbarContainer,
                    ribbonContainer,
                    filterContainer,
                    mainContentContainer
            );


        }

    private void updateLanguage() {
        bundle = TranslationManager.getBundle();
        userFilter.setPromptText(bundle.getString("auditLogs.prompt.user"));
        actionTypeFilter.setPromptText(bundle.getString("auditLogs.prompt.action"));
        startDateFilter.setPromptText(bundle.getString("auditLogs.prompt.from"));
        endDateFilter.setPromptText(bundle.getString("auditLogs.prompt.to"));
        searchButton.setText(bundle.getString("auditLogs.search"));
        resetButton.setText(bundle.getString("auditLogs.reset"));
        exportButton.setText(bundle.getString("auditLogs.export"));

        userFullNameColumn.setText(bundle.getString("auditLogs.column.user"));
        actionTypeColumn.setText(bundle.getString("auditLogs.column.action"));
        detailsColumn.setText(bundle.getString("auditLogs.column.details"));
        timestampColumn.setText(bundle.getString("auditLogs.column.timestamp"));
    }

        private void setupUserFilter(List<User> users) {
            userFilter.setItems(FXCollections.observableArrayList(users));
            userFilter.setCellFactory(cb -> userCell());
            userFilter.setButtonCell(userCell());
        }

        private ListCell<User> userCell() {
            return new ListCell<>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    setText((empty || user == null) ? null : user.getFullName());
                }
            };
        }

        private void setColumnFactories() {
            userFullNameColumn.setCellValueFactory(new PropertyValueFactory<>("userFullName"));
            actionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("actionType"));
            detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
            timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        }


        @FXML
        private void onSearch() {

            try{
            User selectedUser = userFilter.getValue();
            Integer userId = selectedUser != null ? selectedUser.getId() : null;
            String action = actionTypeFilter.getValue();
            LocalDate from = startDateFilter.getValue();
            LocalDate to = endDateFilter.getValue();

            List<AuditLogDTO> logs = service.filterLogs(userId, action, from, to);

            if (logs.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "", bundle.getString("auditLogs.alert.noResults"));
            }
            auditTable.setItems(FXCollections.observableArrayList(logs));

        } catch (Exception e) {
        showAlert(Alert.AlertType.ERROR, "", bundle.getString("auditLogs.alert.searchError"));
        }
        }

        @FXML
        private void onResetFilters() {
            userFilter.setValue(null);
            actionTypeFilter.setValue(null);
            startDateFilter.setValue(null);
            endDateFilter.setValue(null);
            loadLogs(null, null, null, null);
        }

        private void loadLogs(Integer userId, String action, LocalDate from, LocalDate to) {
            try {
                List<AuditLogDTO> logs = service.filterLogs(userId, action, from, to);
                auditTable.setItems(FXCollections.observableArrayList(logs));
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "", bundle.getString("auditLogs.alert.loadError"));
            }
        }

        @FXML
        private void exportToPDF() {
            try {
                service.exportToPDF(auditTable.getItems(), auditTable.getScene().getWindow());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "", bundle.getString("auditLogs.alert.exportError"));
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
