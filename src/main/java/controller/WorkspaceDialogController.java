package controller;

import dto.Workspace.WorkspaceRequestDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Optional;

public class WorkspaceDialogController {

    @FXML private TextField nameField;
    @FXML private TextField capacityField;
    @FXML private TextArea descriptionArea;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    private Stage dialogStage;
    private WorkspaceRequestDTO result;

    private static final int MAX_CAPACITY = 50;

    @FXML
    public void initialize() {
        saveButton.setOnAction(e -> {
            if (validateInputs()) {
                String name = nameField.getText().trim();
                int capacity = Integer.parseInt(capacityField.getText().trim());
                String description = descriptionArea.getText().trim();

                result = new WorkspaceRequestDTO(name, capacity, description);
                dialogStage.close();
            }
        });

        cancelButton.setOnAction(e -> dialogStage.close());
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();


        if (nameField.getText().trim().isEmpty()) {
            errors.append("Workspace name cannot be empty.\n");
        }


        try {
            int cap = Integer.parseInt(capacityField.getText().trim());
            if (cap <= 0) {
                errors.append("Capacity must be greater than 0.\n");
            } else if (cap > MAX_CAPACITY) {
                errors.append("Capacity cannot exceed ").append(MAX_CAPACITY).append(".\n");
            }
        } catch (NumberFormatException e) {
            errors.append("Capacity must be a valid number.\n");
        }


        if (descriptionArea.getText().trim().isEmpty()) {
            errors.append("Description cannot be empty.\n");
        }


        if (errors.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR, errors.toString(), ButtonType.OK);
            alert.showAndWait();
            return false;
        }

        return true;
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public Optional<WorkspaceRequestDTO> getResult() {
        return Optional.ofNullable(result);
    }
}
