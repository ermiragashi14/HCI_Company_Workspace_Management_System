package controller.Staff;

import dto.Workspace.WorkspaceResponseDTO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import service.WorkspaceService;
import service.SessionManager;
import utils.TranslationManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

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
    @FXML private Label availableWorkspacesLabel;

    private final WorkspaceService workspaceService = new WorkspaceService();
    private ResourceBundle bundle;

    @FXML
    public void initialize() {
        bundle = TranslationManager.getBundle();
        TranslationManager.addListener(this::updateLanguage);

        for (int hour = 8; hour <= 18; hour++) {
            startTimeCombo.getItems().add(LocalTime.of(hour, 0));
            endTimeCombo.getItems().add(LocalTime.of(hour, 0));
        }

        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        capacityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCapacity()).asObject());

        checkAvailabilityButton.setOnAction(e -> checkAvailability());

        updateLanguage();
    }

    private void updateLanguage() {
        bundle = TranslationManager.getBundle();

        if (availableWorkspacesLabel != null)
            availableWorkspacesLabel.setText(bundle.getString("staff.available.label"));

        if (startTimeCombo != null)
            startTimeCombo.setPromptText(bundle.getString("new.reservation.startPrompt"));

        if (endTimeCombo != null)
            endTimeCombo.setPromptText(bundle.getString("new.reservation.endPrompt"));

        if (checkAvailabilityButton != null)
            checkAvailabilityButton.setText(bundle.getString("new.reservation.check"));

        if (idColumn != null) idColumn.setText(bundle.getString("table.column.id"));
        if (nameColumn != null) nameColumn.setText(bundle.getString("workspace.table.name"));
        if (descriptionColumn != null) descriptionColumn.setText(bundle.getString("workspace.table.description"));
        if (capacityColumn != null) capacityColumn.setText(bundle.getString("workspace.table.capacity"));
    }

    @FXML
    private void checkAvailability() {
        LocalDate date = datePicker.getValue();
        LocalTime start = startTimeCombo.getValue();
        LocalTime end = endTimeCombo.getValue();

        if (date == null || start == null || end == null || start.isAfter(end)) {
            showAlert(bundle.getString("new.reservation.invalidMessage"));
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
        alert.setTitle(bundle.getString("error.title"));
        alert.setContentText(message);
        alert.showAndWait();
    }
}
