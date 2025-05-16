package controller;

import dto.ManageReservationDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import service.NotificationService;
import service.SessionManager;
import service.ManageReservationsService;
import utils.KeyboardNavigator;
import utils.Navigator;
import utils.TranslationManager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ReservationsManagementController {

    @FXML private TextField userFilterField;
    @FXML private ComboBox<String> workspaceFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private DatePicker dateFilterPicker;
    @FXML private TextField modifiedByFilterField;
    @FXML private DatePicker modifiedDateFilterPicker;

    @FXML private Label userFilterLabel;
    @FXML private Label workspaceFilterLabel;
    @FXML private Label statusFilterLabel;
    @FXML private Label dateFilterLabel;
    @FXML private Label modifiedByFilterLabel;
    @FXML private Label modifiedDateFilterLabel;
    @FXML private Button searchButton;
    @FXML private Button refreshButton;
    @FXML private Button newReservationButton;

    @FXML private TableView<ManageReservationDTO> reservationTable;
    @FXML private TableColumn<ManageReservationDTO, String> userColumn;
    @FXML private TableColumn<ManageReservationDTO, String> workspaceColumn;
    @FXML private TableColumn<ManageReservationDTO, String> dateColumn;
    @FXML private TableColumn<ManageReservationDTO, String> statusColumn;
    @FXML private TableColumn<ManageReservationDTO, String> scheduleColumn;
    @FXML private TableColumn<ManageReservationDTO, String> modifiedByColumn;
    @FXML private TableColumn<ManageReservationDTO, String> modifiedAtColumn;
    @FXML private TableColumn<ManageReservationDTO, Void> actionColumn;
    @FXML private VBox navbarContainer;
    @FXML private TitledPane advancedfilterss;
    @FXML private VBox ribbonContainer;
    @FXML private VBox mainContentContainer;
    @FXML private VBox advancedFiltersContent;


    private final ManageReservationsService service = new ManageReservationsService();
    private ResourceBundle bundle;
    private final NotificationService notificationService = new NotificationService();

    @FXML
    private void initialize() {
        updateLanguage();
        TranslationManager.addListener(this::updateLanguage);

        setupComboBoxes();
        setupTableColumns();
        loadAllReservations();
        loadNavbar();
        advancedfilterss.setFocusTraversable(true);
        KeyboardNavigator.enableTitledPaneKeyboardSupport(advancedfilterss, advancedFiltersContent);
        KeyboardNavigator.enableAdvancedNavigation(ribbonContainer,navbarContainer,mainContentContainer,advancedfilterss,advancedFiltersContent);
    }

    public void updateLanguage() {
        bundle = TranslationManager.getBundle();

        userColumn.setText(bundle.getString("reservation.management.userColumn"));
        workspaceColumn.setText(bundle.getString("reservation.management.workspaceColumn"));
        dateColumn.setText(bundle.getString("reservation.management.dateColumn"));
        statusColumn.setText(bundle.getString("reservation.management.statusColumn"));
        scheduleColumn.setText(bundle.getString("reservation.management.scheduleColumn"));
        modifiedByColumn.setText(bundle.getString("reservation.management.modifiedByColumn"));
        modifiedAtColumn.setText(bundle.getString("reservation.management.modifiedAtColumn"));
        actionColumn.setText(bundle.getString("reservation.management.actionColumn"));

        userFilterLabel.setText(bundle.getString("reservation.management.userColumn"));
        workspaceFilterLabel.setText(bundle.getString("reservation.management.workspaceColumn"));
        statusFilterLabel.setText(bundle.getString("reservation.management.statusColumn"));
        dateFilterLabel.setText(bundle.getString("reservation.management.dateColumn"));
        modifiedByFilterLabel.setText(bundle.getString("reservation.management.modifiedByColumn"));
        modifiedDateFilterLabel.setText(bundle.getString("reservation.management.modifiedAtColumn"));

        searchButton.setText(bundle.getString("manage.users.search"));
        refreshButton.setText(bundle.getString("manage.users.refresh"));
        newReservationButton.setText(bundle.getString("reservation.management.newreservation"));
        advancedfilterss.setText(bundle.getString("advanced.filters"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource(navbarPath), TranslationManager.getBundle());
            Node navbar = loader.load();
            navbarContainer.getChildren().setAll(navbar);
            System.out.println("[DEBUG] Loading navbar for role: " + role);
        } catch (IOException e) {
            System.err.println("[ERROR] Unknown role: " + role);
            e.printStackTrace();
        }
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
            private final Button cancelBtn = new Button();

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                cancelBtn.setText(bundle.getString("reservation.management.cancelButton"));

                ManageReservationDTO reservation = getTableView().getItems().get(getIndex());
                cancelBtn.setDisable(!"CONFIRMED".equalsIgnoreCase(reservation.getStatus()));

                cancelBtn.setOnAction(e -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, bundle.getString("reservation.management.cancelConfirm"), ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> result = confirm.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.YES) {
                        try {
                            boolean success = service.cancelReservation(reservation.getId(), SessionManager.getInstance().getLoggedInUserId());
                            if (success) {

                                String message = "Your reservation for " + reservation.getWorkspaceName() +
                                        " on " + reservation.getDate() + " has been canceled.";

                                notificationService.sendSystemNotification(
                                        reservation.getUserId(),
                                        "INFO",
                                        message
                                );

                                onSearchClicked();
                                onSearchClicked();
                            } else {
                                showAlert(Alert.AlertType.ERROR, "", bundle.getString("reservation.management.cancelFailed"));
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showAlert(Alert.AlertType.ERROR, "", bundle.getString("reservation.management.cancelError"));
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
