package controller;

import dto.NotificationDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.User;
import service.NotificationService;
import service.SessionManager;
import utils.KeyboardNavigator;
import utils.Navigator;

public class SendNotificationController {

    @FXML private TextArea messageField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private RadioButton sendToAllRadio;
    @FXML private RadioButton sendToOneRadio;
    @FXML private ComboBox<User> userComboBox;
    @FXML private Button backButton;
    @FXML private VBox notificationsPage;

    private final NotificationService notificationService = new NotificationService();

    @FXML
    public void initialize() {

        typeComboBox.setItems(FXCollections.observableArrayList("INFO", "REMINDER", "ACTION", "WARNING"));
        typeComboBox.getSelectionModel().selectFirst();

        userComboBox.setItems(FXCollections.observableArrayList(notificationService.getAllOtherUsersInCompany()));
        userComboBox.setVisible(false);

        ToggleGroup group = new ToggleGroup();
        sendToAllRadio.setToggleGroup(group);
        sendToOneRadio.setToggleGroup(group);

        group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            userComboBox.setVisible(sendToOneRadio.isSelected());
        });
        KeyboardNavigator.enableNavigation(notificationsPage);
    }

    @FXML
    private void handleSendNotification() {
        String message = messageField.getText().trim();
        String type = typeComboBox.getValue();
        int senderId = SessionManager.getInstance().getLoggedInUserId();

        if (message.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Message cannot be empty.");
            return;
        }

        if (sendToAllRadio.isSelected()) {
            NotificationDTO dto = new NotificationDTO(-1, senderId, message, type);
            notificationService.sendToAll(dto);
            showAlert(Alert.AlertType.INFORMATION, "Notification sent to all users.");
        } else {
            User selectedUser = userComboBox.getValue();
            if (selectedUser == null) {
                showAlert(Alert.AlertType.WARNING, "Please select a user.");
                return;
            }
            NotificationDTO dto = new NotificationDTO(selectedUser.getId(), senderId, message, type);
            notificationService.sendToUser(dto);
            showAlert(Alert.AlertType.INFORMATION, "Notification sent to selected user.");
        }
        resetForm();
    }

    private void resetForm() {
        messageField.clear();
        typeComboBox.getSelectionModel().selectFirst();
        userComboBox.getSelectionModel().clearSelection();
        userComboBox.setVisible(false);
        sendToAllRadio.setSelected(true);
    }
    @FXML
    private void handleBackButton(){
        Navigator.navigateTo("notifications.fxml", backButton);
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
