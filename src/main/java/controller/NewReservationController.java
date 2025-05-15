package controller;

import dto.NewReservationDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import service.NewReservationService;
import service.NotificationService;
import service.SessionManager;
import utils.KeyboardNavigator;
import utils.Navigator;
import utils.TranslationManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

public class NewReservationController {
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<LocalTime> startTimeCombo;
    @FXML private ComboBox<LocalTime> endTimeCombo;
    @FXML private ListView<NewReservationDTO> availableWorkspacesList;
    @FXML private Button reserveButton;
    @FXML private Button cancelButton;
    @FXML private Button checkAvailabilityButton;
    @FXML private Label startTimeLabel;
    @FXML private Label endTimeLabel;
    @FXML private Button backButton;
    @FXML private VBox navbarContainer;
    @FXML private VBox ribbonContainer;
    @FXML private AnchorPane NewRez;

    private final NewReservationService service = new NewReservationService();
    ResourceBundle bundle;
    private final NotificationService notificationService = new NotificationService();

    @FXML
    public void initialize() {

        for (int hour = 8; hour <= 18; hour++) {
            startTimeCombo.getItems().add(LocalTime.of(hour, 0));
            endTimeCombo.getItems().add(LocalTime.of(hour, 0));
        }

        String role = SessionManager.getInstance().getLoggedInUserRole();
        boolean isStaff = "staff".equalsIgnoreCase(role);

        navbarContainer.setVisible(isStaff);
        navbarContainer.setManaged(isStaff);
        ribbonContainer.setVisible(isStaff);
        ribbonContainer.setManaged(isStaff);

        backButton.setVisible(!isStaff);
        backButton.setManaged(!isStaff);
        
        updateLanguage();
        TranslationManager.addListener(this::updateLanguage);
        KeyboardNavigator.enableNavigation(NewRez);
    }

    private void updateLanguage() {
        bundle=TranslationManager.getBundle();
        reserveButton.setText(bundle.getString("new.reservation.reserve"));
        cancelButton.setText(bundle.getString("new.reservation.cancel"));
        backButton.setText(bundle.getString("new.reservation.back"));
        checkAvailabilityButton.setText(bundle.getString("new.reservation.check"));
        startTimeCombo.setPromptText(bundle.getString("new.reservation.startPrompt"));
        endTimeCombo.setPromptText(bundle.getString("new.reservation.endPrompt"));
    }

    @FXML
    public void onCheckAvailabilityClicked() {

        LocalDate date = datePicker.getValue();
        LocalTime start = startTimeCombo.getValue();
        LocalTime end = endTimeCombo.getValue();

        if (date == null || start == null || end == null || !start.isBefore(end)) {
            showAlert(bundle.getString("new.reservation.invalidTitle"), bundle.getString("new.reservation.invalidMessage"));
            return;
        }

        List<NewReservationDTO> available = service.getAvailableWorkspaces(
                date, start, end, SessionManager.getInstance().getLoggedInCompanyId()
        );

        if (available.isEmpty()) {
            showAlert(bundle.getString("new.reservation.noneAvailableTitle"), bundle.getString("new.reservation.noneAvailableMessage"));
        }

        availableWorkspacesList.setItems(FXCollections.observableArrayList(available));
    }

    @FXML
    public void onReserveClicked() {

        NewReservationDTO selected = availableWorkspacesList.getSelectionModel().getSelectedItem();
        LocalDate date = datePicker.getValue();
        LocalTime start = startTimeCombo.getValue();
        LocalTime end = endTimeCombo.getValue();

        if (selected == null || date == null || start == null || end == null) {
            showAlert(bundle.getString("new.reservation.missingTitle"), bundle.getString("new.reservation.missingMessage"));
            return;
        }

        boolean success = service.createReservation(
                SessionManager.getInstance().getLoggedInUserId(),
                selected.getWorkspaceId(), date, start, end
        );


        if (success) {
            notificationService.sendSystemNotification(
                    SessionManager.getInstance().getLoggedInUserId(),
                    "INFO",
                    "Your reservation for " + selected.getWorkspaceName() + " on " + date + " has been confirmed."
            );

            showAlert(bundle.getString("new.reservation.successTitle"), bundle.getString("new.reservation.successMessage"));
        } else {
            showAlert(bundle.getString("new.reservation.errorTitle"), bundle.getString("new.reservation.errorMessage"));
        }
    }

    @FXML
    public void onCancelClicked() {

        datePicker.setValue(null);
        startTimeCombo.setValue(null);
        endTimeCombo.setValue(null);
        availableWorkspacesList.getItems().clear();
    }

    @FXML
    public void onBackClicked() {
        Navigator.navigateTo("reservation_management.fxml", backButton);
    }

    private void showAlert(String title, String content) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
