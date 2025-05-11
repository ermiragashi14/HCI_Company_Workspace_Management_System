package controller;

import dto.Workspace.WorkspaceRequestDTO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utils.TranslationManager;

import java.util.Optional;
import java.util.ResourceBundle;

public class WorkspaceDialogController {

    @FXML private TextField nameField;
    @FXML private TextField capacityField;
    @FXML private TextArea descriptionArea;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;
    @FXML private Label nameLabel;
    @FXML private Label capacityLabel;
    @FXML private Label descriptionLabel;

    private Stage dialogStage;
    private WorkspaceRequestDTO result;

    private static final int MAX_CAPACITY = 50;
    private ResourceBundle bundle;

    @FXML
    public void initialize() {

        bundle = TranslationManager.getBundle();
        updateLanguage();
        TranslationManager.addListener(this::updateLanguage);

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

    private void updateLanguage() {

        bundle = TranslationManager.getBundle();
        nameLabel.setText(bundle.getString("workspace.dialog.name"));
        capacityLabel.setText(bundle.getString("workspace.dialog.capacity"));
        descriptionLabel.setText(bundle.getString("workspace.dialog.description"));
        cancelButton.setText(bundle.getString("workspace.dialog.cancel"));
        saveButton.setText(bundle.getString("workspace.dialog.save"));
        nameField.setPromptText(bundle.getString("workspace.dialog.name.placeholder"));
        capacityField.setPromptText(bundle.getString("workspace.dialog.capacity.placeholder"));
        descriptionArea.setPromptText(bundle.getString("workspace.dialog.description.placeholder"));
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();

        if (nameField.getText().trim().isEmpty()) {
            errors.append(bundle.getString("workspace.dialog.error.nameEmpty")).append("\n");
        }

        try {
            int cap = Integer.parseInt(capacityField.getText().trim());
            if (cap <= 0) {
                errors.append(bundle.getString("workspace.dialog.error.capBelowZero")).append("\n");
            } else if (cap > MAX_CAPACITY) {
                errors.append(bundle.getString("workspace.dialog.error.capTooHigh")
                        .replace("{0}", String.valueOf(MAX_CAPACITY))).append("\n");
            }
        } catch (NumberFormatException e) {
            errors.append(bundle.getString("workspace.dialog.error.capInvalid")).append("\n");
        }

        if (descriptionArea.getText().trim().isEmpty()) {
            errors.append(bundle.getString("workspace.dialog.error.descEmpty")).append("\n");
        }

        if (errors.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR, errors.toString(), ButtonType.OK);
            alert.setTitle(bundle.getString("error.title"));
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
