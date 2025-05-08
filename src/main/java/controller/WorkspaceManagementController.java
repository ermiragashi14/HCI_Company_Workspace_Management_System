package controller;

import dto.Workspace.WorkspaceRequestDTO;
import dto.Workspace.WorkspaceResponseDTO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import service.SessionManager;
import service.WorkspaceService;
import javafx.scene.control.cell.TextFieldTableCell;
import utils.TranslationUtils;


import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

public class WorkspaceManagementController {

    @FXML private TableView<WorkspaceResponseDTO> workspaceTable;
    @FXML private TableColumn<WorkspaceResponseDTO, String> nameColumn;
    @FXML private TableColumn<WorkspaceResponseDTO, Integer> capacityColumn;
    @FXML private TableColumn<WorkspaceResponseDTO, String> descriptionColumn;
    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private VBox navbarContainer;


    private final WorkspaceService service = new WorkspaceService();
    private ObservableList<WorkspaceResponseDTO> data;
    private ResourceBundle bundle;

    @FXML
    public void initialize() {
        bundle = TranslationUtils.getBundle();
        workspaceTable.setEditable(true);
        setupColumns();
        loadWorkspaces();
        localizeUI();
        loadNavbar();


        addButton.setOnAction(e -> handleAdd());
        deleteButton.setOnAction(e -> handleDelete());
        TranslationUtils.addListener(this::localizeUI);
    }
    private void loadNavbar() {
        String role = SessionManager.getInstance().getLoggedInUserRole();

        String navbarPath = switch (role.toUpperCase()) {
            case "ADMIN" -> "/views/admin_navbar.fxml";
            case "SUPER_ADMIN" -> "/views/superadmin_navbar.fxml";
            default -> null;
        };
        if (navbarPath == null) {
            System.err.println("Unknown user role: " + role);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(navbarPath), TranslationUtils.getBundle());
            Node navbar = loader.load();
            navbarContainer.getChildren().setAll(navbar);
            System.out.println("[DEBUG] Loading navbar for role: " + role);

        } catch (IOException e) {
            System.err.println("[ERROR] Unknown role: " + role);
            e.printStackTrace();
        }
    }


    private void localizeUI() {
        bundle = TranslationUtils.getBundle();
        nameColumn.setText(bundle.getString("workspace.table.name"));
        capacityColumn.setText(bundle.getString("workspace.table.capacity"));
        descriptionColumn.setText(bundle.getString("workspace.table.description"));
        addButton.setText(bundle.getString("workspace.button.add"));
        deleteButton.setText(bundle.getString("workspace.button.delete"));
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/workspace_dialog.fxml"), TranslationUtils.getBundle());
            VBox root = loader.load();

            WorkspaceDialogController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(bundle.getString("workspace.dialog.title"));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            controller.getResult().ifPresent(dto -> {
                int companyId = SessionManager.getInstance().getLoggedInCompanyId();
                service.createWorkspace(dto, companyId);
                loadWorkspaces();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDelete() {
        WorkspaceResponseDTO selected = workspaceTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    bundle.getString("workspace.confirm.delete"), ButtonType.YES, ButtonType.NO);
            confirm.setTitle(bundle.getString("workspace.confirm.title"));
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
