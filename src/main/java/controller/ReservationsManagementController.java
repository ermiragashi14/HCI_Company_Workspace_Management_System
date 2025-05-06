package controller;

import dto.ManageReservationDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import service.SessionManager;
import service.ManageReservationsService;
import utils.Navigator;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ReservationsManagementController {

    @FXML private TextField userFilterField;
    @FXML private ComboBox<String> workspaceFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private DatePicker dateFilterPicker;
    @FXML private TextField modifiedByFilterField;
    @FXML private DatePicker modifiedDateFilterPicker;

    @FXML private TableView<ManageReservationDTO> reservationTable;
    @FXML private TableColumn<ManageReservationDTO, String> userColumn;
    @FXML private TableColumn<ManageReservationDTO, String> workspaceColumn;
    @FXML private TableColumn<ManageReservationDTO, String> dateColumn;
    @FXML private TableColumn<ManageReservationDTO, String> statusColumn;
    @FXML private TableColumn<ManageReservationDTO, String> scheduleColumn;
    @FXML private TableColumn<ManageReservationDTO, String> modifiedByColumn;
    @FXML private TableColumn<ManageReservationDTO, String> modifiedAtColumn;
    @FXML private TableColumn<ManageReservationDTO, Void> actionColumn;

    private final ManageReservationsService service = new ManageReservationsService();

    @FXML
    private void initialize() {
        setupComboBoxes();
        setupTableColumns();
        loadAllReservations();
        applyRoleBasedAccessControl();
    }

    private void setupComboBoxes() {
        statusFilterCombo.setItems(FXCollections.observableArrayList("ALL", "CONFIRMED", "CANCELED", "EXPIRED"));
        statusFilterCombo.getSelectionModel().select("ALL");

        List<String> workspaces = service.getAllWorkspaceNames(SessionManager.getInstance().getLoggedInCompanyId());
        workspaces.add(0, "ALL");
        workspaceFilterCombo.setItems(FXCollections.observableArrayList(workspaces));
        workspaceFilterCombo.getSelectionModel().select("ALL");
    }

    private void setupTableColumns() {
        userColumn.setCellValueFactory(new PropertyValueFactory<>("userFullName"));
        workspaceColumn.setCellValueFactory(new PropertyValueFactory<>("workspaceName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        scheduleColumn.setCellValueFactory(new PropertyValueFactory<>("schedule"));
        modifiedByColumn.setCellValueFactory(new PropertyValueFactory<>("modifiedBy"));
        modifiedAtColumn.setCellValueFactory(new PropertyValueFactory<>("modifiedAt"));

        addActionButtons();
    }

    private void addActionButtons() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button cancelBtn = new Button("❌ Cancel");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                ManageReservationDTO reservation = getTableView().getItems().get(getIndex());

                cancelBtn.setDisable(!"CONFIRMED".equalsIgnoreCase(reservation.getStatus()));

                cancelBtn.setOnAction(e -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel this reservation?", ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> result = confirm.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.YES) {
                        boolean success = service.cancelReservation(reservation.getId(), SessionManager.getInstance().getLoggedInUserId());
                        if (success) {
                            onSearchClicked();
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Failed", "Could not cancel reservation.");
                        }
                    }
                });

                setGraphic(cancelBtn);
            }
        });
    }

    @FXML
    private void onSearchClicked() {
        String user = emptyOrNull(userFilterField.getText());
        String workspace = workspaceFilterCombo.getValue();
        if ("ALL".equalsIgnoreCase(workspace)) workspace = null;

        String status = statusFilterCombo.getValue();
        if ("ALL".equalsIgnoreCase(status)) status = null;

        LocalDate date = dateFilterPicker.getValue();
        String modifiedBy = emptyOrNull(modifiedByFilterField.getText());
        LocalDate modifiedAt = modifiedDateFilterPicker.getValue();

        List<ManageReservationDTO> reservations = service.filterReservations(
                SessionManager.getInstance().getLoggedInCompanyId(),
                user, workspace, status, date, modifiedBy, modifiedAt
        );

        reservationTable.setItems(FXCollections.observableArrayList(reservations));
    }

    @FXML
    private void onRefreshClicked() {
        userFilterField.clear();
        workspaceFilterCombo.getSelectionModel().select("ALL");
        statusFilterCombo.getSelectionModel().select("ALL");
        dateFilterPicker.setValue(null);
        modifiedByFilterField.clear();
        modifiedDateFilterPicker.setValue(null);
        loadAllReservations();
    }

    @FXML
    private void onNewReservationClicked() {
        Navigator.navigateTo("new_reservation.fxml", reservationTable);
    }

    private void loadAllReservations() {
        List<ManageReservationDTO> reservations = service.filterReservations(
                SessionManager.getInstance().getLoggedInCompanyId(),
                null, null, null, null, null, null
        );
        reservationTable.setItems(FXCollections.observableArrayList(reservations));
    }

    private void applyRoleBasedAccessControl() {
        if ("ADMIN".equals(SessionManager.getInstance().getLoggedInUserRole())) {
            // Hide or disable SuperAdmin-only features (if any)
        }
    }

    private String emptyOrNull(String input) {
        return (input != null && !input.isBlank()) ? input.trim() : null;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
