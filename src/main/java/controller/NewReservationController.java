package controller;

import dto.NewReservationDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import service.NewReservationService;
import service.SessionManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class NewReservationController {
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<LocalTime> startTimeCombo;
    @FXML private ComboBox<LocalTime> endTimeCombo;
    @FXML private ListView<NewReservationDTO> availableWorkspacesList;

    private final NewReservationService service = new NewReservationService();

    @FXML
    public void initialize() {

        for (int hour = 8; hour <= 18; hour++) {
            startTimeCombo.getItems().add(LocalTime.of(hour, 0));
            endTimeCombo.getItems().add(LocalTime.of(hour, 0));
        }
    }

    @FXML
    public void onCheckAvailabilityClicked() {

        LocalDate date = datePicker.getValue();
        LocalTime start = startTimeCombo.getValue();
        LocalTime end = endTimeCombo.getValue();

        if (date == null || start == null || end == null || !start.isBefore(end)) {
            showAlert("Invalid input", "Please select a valid date and time range.");
            return;
        }

        List<NewReservationDTO> available = service.getAvailableWorkspaces(
                date, start, end, SessionManager.getInstance().getLoggedInCompanyId()
        );
        availableWorkspacesList.setItems(FXCollections.observableArrayList(available));
    }

    @FXML
    public void onReserveClicked() {

        NewReservationDTO selected = availableWorkspacesList.getSelectionModel().getSelectedItem();
        LocalDate date = datePicker.getValue();
        LocalTime start = startTimeCombo.getValue();
        LocalTime end = endTimeCombo.getValue();

        if (selected == null || date == null || start == null || end == null) {
            showAlert("Missing input", "Select a workspace and time range.");
            return;
        }

        boolean success = service.createReservation(
                SessionManager.getInstance().getLoggedInUserId(),
                selected.getWorkspaceId(), date, start, end
        );

        if (success) {
            showAlert("Success", "Reservation confirmed.");
        } else {
            showAlert("Error", "Could not complete reservation.");
        }
    }

    @FXML
    public void onCancelClicked() {

        datePicker.setValue(null);
        startTimeCombo.setValue(null);
        endTimeCombo.setValue(null);
        availableWorkspacesList.getItems().clear();
    }

    private void showAlert(String title, String content) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
