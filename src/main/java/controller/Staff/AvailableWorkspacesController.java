package controller.Staff;

import dto.Workspace.WorkspaceResponseDTO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import service.WorkspaceService;
import service.SessionManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AvailableWorkspacesController {

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<LocalTime> startTimeCombo;
    @FXML private ComboBox<LocalTime> endTimeCombo;
    @FXML private Button checkAvailabilityButton;

    @FXML private TableView<WorkspaceResponseDTO> workspaceTable;
    @FXML private TableColumn<WorkspaceResponseDTO, Integer> idColumn;
    @FXML private TableColumn<WorkspaceResponseDTO, String> nameColumn;
    @FXML private TableColumn<WorkspaceResponseDTO, String> descriptionColumn;
    @FXML private TableColumn<WorkspaceResponseDTO, Integer> capacityColumn;

    private final WorkspaceService workspaceService = new WorkspaceService();

    @FXML
    public void initialize() {
        for (int hour = 8; hour <= 18; hour++) {
            startTimeCombo.getItems().add(LocalTime.of(hour, 0));
            endTimeCombo.getItems().add(LocalTime.of(hour, 0));
        }

        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        capacityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCapacity()).asObject());

        checkAvailabilityButton.setOnAction(e -> checkAvailability());
    }

    @FXML
    private void checkAvailability() {
        LocalDate date = datePicker.getValue();
        LocalTime start = startTimeCombo.getValue();
        LocalTime end = endTimeCombo.getValue();

        if (date == null || start == null || end == null || start.isAfter(end)) {
            showAlert("Please select valid date and time intervals.");
            return;
        }

        int companyId = SessionManager.getInstance().getLoggedInCompanyId();

        List<WorkspaceResponseDTO> availableWorkspaces =
                workspaceService.getAvailableWorkspaces(companyId, date, start, end);

        workspaceTable.getItems().clear();
        workspaceTable.getItems().addAll(availableWorkspaces);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
