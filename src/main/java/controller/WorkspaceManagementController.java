package controller;

import dto.Workspace.WorkspaceRequestDTO;
import dto.Workspace.WorkspaceResponseDTO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.converter.IntegerStringConverter;
import service.SessionManager;
import service.WorkspaceService;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.List;

public class WorkspaceManagementController {

    @FXML private TableView<WorkspaceResponseDTO> workspaceTable;
    @FXML private TableColumn<WorkspaceResponseDTO, String> nameColumn;
    @FXML private TableColumn<WorkspaceResponseDTO, Integer> capacityColumn;
    @FXML private TableColumn<WorkspaceResponseDTO, String> descriptionColumn;
    @FXML private Button addButton;
    @FXML private Button deleteButton;

    private final WorkspaceService service = new WorkspaceService();
    private ObservableList<WorkspaceResponseDTO> data;

    @FXML
    public void initialize() {
        workspaceTable.setEditable(true);
        setupColumns();
        loadWorkspaces();

        addButton.setOnAction(e -> handleAdd());
        deleteButton.setOnAction(e -> handleDelete());
    }

    private void loadWorkspaces() {
        List<WorkspaceResponseDTO> list = service.getAllWorkspacesForLoggedInCompany();
        data = FXCollections.observableArrayList(list);
        workspaceTable.setItems(data);
    }

    private void setupColumns() {
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        capacityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCapacity()).asObject());
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        capacityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        nameColumn.setOnEditCommit(event -> {
            WorkspaceResponseDTO workspace = event.getRowValue();
            workspace.setName(event.getNewValue());
            updateWorkspace(workspace);
        });

        capacityColumn.setOnEditCommit(event -> {
            WorkspaceResponseDTO workspace = event.getRowValue();
            workspace.setCapacity(event.getNewValue());
            updateWorkspace(workspace);
        });

        descriptionColumn.setOnEditCommit(event -> {
            WorkspaceResponseDTO workspace = event.getRowValue();
            workspace.setDescription(event.getNewValue());
            updateWorkspace(workspace);
        });
    }

    private void handleAdd() {
        int companyId = SessionManager.getInstance().getLoggedInCompanyId();
        WorkspaceRequestDTO newWs = new WorkspaceRequestDTO("New Room", 10, "Placeholder");
        service.createWorkspace(newWs, companyId);
        loadWorkspaces();
    }

    private void handleDelete() {
        WorkspaceResponseDTO selected = workspaceTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete this workspace?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    service.deleteWorkspace(selected.getId());
                    loadWorkspaces();
                }
            });
        }
    }

    private void updateWorkspace(WorkspaceResponseDTO dto) {
        WorkspaceRequestDTO updated = new WorkspaceRequestDTO(dto.getName(), dto.getCapacity(), dto.getDescription());
        service.updateWorkspace(dto.getId(), updated);
    }
}
