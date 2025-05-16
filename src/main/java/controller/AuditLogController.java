package controller;

import java.util.List;

import dto.AuditLogDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import service.AuditLogService;
import service.SessionManager;
import utils.Navigator;
import java.time.LocalDate;

    public class AuditLogController {

        @FXML private TextField userIdFilter;
        @FXML private ComboBox<String> actionTypeFilter;
        @FXML private DatePicker startDateFilter;
        @FXML private DatePicker endDateFilter;
        @FXML private TableView<AuditLogDTO> auditTable;
        @FXML private TableColumn<AuditLogDTO, String> userFullNameColumn;
        @FXML private TableColumn<AuditLogDTO, String> actionTypeColumn;
        @FXML private TableColumn<AuditLogDTO, String> detailsColumn;
        @FXML private TableColumn<AuditLogDTO, String> timestampColumn;
        @FXML private Label companyNameLabel;

        private final AuditLogService service = new AuditLogService();

        @FXML
        public void initialize() {
            companyNameLabel.setText(service.getCompanyName(SessionManager.getInstance().getLoggedInCompanyId()));
            actionTypeFilter.setItems(FXCollections.observableArrayList("INFO","UPDATE", "DELETE", "CREATE"));
            userFullNameColumn.setCellValueFactory(new PropertyValueFactory<>("userFullName"));
            actionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("actionType"));
            detailsColumn.setCellValueFactory(new PropertyValueFactory<>("details"));
            timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

            loadLogs(null, null, null, null);
        }

        @FXML
        private void onSearch() {
            String user = userIdFilter.getText().trim();
            String action = actionTypeFilter.getValue();
            LocalDate from = startDateFilter.getValue();
            LocalDate to = endDateFilter.getValue();

            loadLogs(user, action, from, to);
        }

        @FXML
        private void onResetFilters() {
            userIdFilter.clear();
            actionTypeFilter.setValue(null);
            startDateFilter.setValue(null);
            endDateFilter.setValue(null);
            loadLogs(null, null, null, null);
        }

        private void loadLogs(String user, String action, LocalDate from, LocalDate to) {
            List<AuditLogDTO> logs = service.getAuditLogs(SessionManager.getInstance().getLoggedInCompanyId(), user, action, from, to);
            auditTable.setItems(FXCollections.observableArrayList(logs));
        }

        @FXML
        private void exportToPDF() {
            service.exportToPDF(auditTable.getItems());
        }

        @FXML
        private void goBack() {
            Navigator.navigateTo("superadmin_dashboard.fxml", auditTable);
        }
}
