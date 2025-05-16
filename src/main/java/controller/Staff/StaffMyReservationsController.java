package controller.Staff;

import dto.ManageReservationDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import service.ManageReservationsService;
import service.SessionManager;
import utils.KeyboardNavigator;
import utils.TranslationManager;

import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class StaffMyReservationsController {

    @FXML private TableView<ManageReservationDTO> reservationTable;
    @FXML private TableColumn<ManageReservationDTO, String> workspaceColumn;
    @FXML private TableColumn<ManageReservationDTO, String> dateColumn;
    @FXML private TableColumn<ManageReservationDTO, String> startTimeColumn;
    @FXML private TableColumn<ManageReservationDTO, String> endTimeColumn;
    @FXML private TableColumn<ManageReservationDTO, String> statusColumn;
    @FXML private Label myReservationsLabel;
    @FXML private Button cancelButton;
    @FXML private Button refreshButton;

    @FXML private VBox navbarContainer;
    @FXML private VBox ribbonContainer;
    @FXML private VBox mainContentContainer;

    private final ManageReservationsService service = new ManageReservationsService();
    private ResourceBundle bundle;

    @FXML
    public void initialize() {
        bundle = TranslationManager.getBundle();
        TranslationManager.addListener(this::updateLanguage);
        setupTableColumns();
        loadUserReservations();

        cancelButton.setOnAction(e -> handleCancel());
        refreshButton.setOnAction(e -> loadUserReservations());

        updateLanguage();
        KeyboardNavigator.enableFullNavigation(false, navbarContainer, ribbonContainer, mainContentContainer);
    }

    private void updateLanguage() {
        bundle = TranslationManager.getBundle();

        if (workspaceColumn != null)
            workspaceColumn.setText(bundle.getString("reservation.management.workspaceColumn"));
        if (dateColumn != null)
            dateColumn.setText(bundle.getString("reservation.management.dateColumn"));
        if (startTimeColumn != null)
            startTimeColumn.setText(bundle.getString("reservation.management.startTime"));
        if (endTimeColumn != null)
            endTimeColumn.setText(bundle.getString("reservation.management.endTime"));
        if (statusColumn != null)
            statusColumn.setText(bundle.getString("reservation.management.statusColumn"));
        if (cancelButton != null)
            cancelButton.setText(bundle.getString("reservation.management.cancelButton"));
        if (refreshButton != null)
            refreshButton.setText(bundle.getString("manage.users.refresh"));
        if (myReservationsLabel != null)
            myReservationsLabel.setText(bundle.getString("staff.myreservations.title"));
    }

    private void setupTableColumns() {
        workspaceColumn.setCellValueFactory(data -> data.getValue().workspaceNameProperty());
        dateColumn.setCellValueFactory(data -> data.getValue().dateProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());

        startTimeColumn.setCellValueFactory(data -> {
            String[] parts = data.getValue().getSchedule().split(" - ");
            return new javafx.beans.property.SimpleStringProperty(parts[0]);
        });
        endTimeColumn.setCellValueFactory(data -> {
            String[] parts = data.getValue().getSchedule().split(" - ");
            return new javafx.beans.property.SimpleStringProperty(parts.length > 1 ? parts[1] : "");
        });
    }

    private void loadUserReservations() {
        int userId = SessionManager.getInstance().getLoggedInUserId();
        List<ManageReservationDTO> reservations = service.getReservationsByUserId(userId);
        reservationTable.setItems(FXCollections.observableArrayList(reservations));
    }

    private void handleCancel() {
        ManageReservationDTO selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null || !"CONFIRMED".equalsIgnoreCase(selected.getStatus())) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                bundle.getString("reservation.management.cancelConfirm"), ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            boolean success = service.cancelReservation(selected.getId(), SessionManager.getInstance().getLoggedInUserId());
            if (success) {
                loadUserReservations();
            } else {
                showAlert(Alert.AlertType.ERROR, bundle.getString("reservation.management.cancelFailed"));
            }
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
