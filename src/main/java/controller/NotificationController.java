package controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Notification;
import service.NotificationService;
import service.SessionManager;
import utils.KeyboardNavigator;
import utils.Navigator;
import java.util.List;

public class NotificationController {

    @FXML private TableView<Notification> notificationsTable;
    @FXML private TableColumn<Notification, String> fromColumn;
    @FXML private TableColumn<Notification, String> toColumn;
    @FXML private TableColumn<Notification, String> messageColumn;
    @FXML private TableColumn<Notification, String> typeColumn;
    @FXML private TableColumn<Notification, String> readColumn;
    @FXML private TableColumn<Notification, String> sentAtColumn;

    @FXML private TextField senderFilter;
    @FXML private TextField receiverFilter;
    @FXML private ComboBox<String> typeFilter;
    @FXML private ComboBox<String> readStatusFilter;
    @FXML private DatePicker dateFilter;
    @FXML private Button sendBtn;
    @FXML private Button goBackBtn;
    @FXML private AnchorPane notify;

    private NotificationService notificationService=new NotificationService();
    private FilteredList<Notification> filteredData;

    @FXML
    public void initialize() {
        String role=SessionManager.getInstance().getLoggedInUserRole();
        if(equals("STAFF".equals(role))){
            sendBtn.setVisible(false);}
        setupTable();
        loadNotifications();
        setupFilters();
        KeyboardNavigator.enableNavigation(notify);
    }

    private void setupTable() {
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("senderName"));
        toColumn.setCellValueFactory(new PropertyValueFactory<>("receiverName"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        readColumn.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().isReadStatus() ? "Read" : "Unread"));
        sentAtColumn.setCellValueFactory(new PropertyValueFactory<>("sentAt"));
    }

    private void loadNotifications() {
        int userId = SessionManager.getInstance().getLoggedInUserId();

        List<Notification> notifications = notificationService.getUserNotifications(userId);

        filteredData = new FilteredList<>(FXCollections.observableArrayList(notifications), p -> true);
        notificationsTable.setItems(new SortedList<>(filteredData));
    }

    private void setupFilters() {
        typeFilter.setItems(FXCollections.observableArrayList("INFO", "REMINDER", "ACTION", "WARNING"));
        readStatusFilter.setItems(FXCollections.observableArrayList( "Read", "Unread"));
    }

    @FXML
    private void applyFilters() {
        filteredData.setPredicate(notification -> {
            boolean senderMatch = senderFilter.getText().isEmpty() ||
                    notification.getSenderName().toLowerCase().contains(senderFilter.getText().toLowerCase());
            boolean receiverMatch = receiverFilter.getText().isEmpty() ||
                    notification.getReceiverName().toLowerCase().contains(receiverFilter.getText().toLowerCase());
            boolean typeMatch = typeFilter.getValue() == null || typeFilter.getValue().isEmpty() ||
                    typeFilter.getValue().equals(notification.getType());
            boolean readMatch = readStatusFilter.getValue() == null || readStatusFilter.getValue().isEmpty() ||
                    (readStatusFilter.getValue().equals("Read") && notification.isReadStatus()) ||
                    (readStatusFilter.getValue().equals("Unread") && !notification.isReadStatus());
            boolean dateMatch = dateFilter.getValue() == null ||
                    notification.getSentAt().toLocalDate().equals(dateFilter.getValue());

            return senderMatch && receiverMatch && typeMatch && readMatch && dateMatch;
        });
    }

    @FXML
    private void clearFilters() {
        senderFilter.clear();
        receiverFilter.clear();
        typeFilter.getSelectionModel().clearSelection();
        readStatusFilter.getSelectionModel().clearSelection();
        dateFilter.setValue(null);
        applyFilters();
    }

    @FXML
    private void handleMarkAsRead() {
        Notification selected = notificationsTable.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.isReadStatus()) {
            boolean success = notificationService.markNotificationAsRead(selected.getId());
            if (success) {
                selected.setReadStatus(true);
                notificationsTable.refresh();
            } else {
                showAlert("You can't mark this as read. You're not the receiver.");
            }
        }
    }

    @FXML
    private void goToSendNotification() {
        Navigator.navigateTo("send_notifications.fxml", sendBtn);
    }

    @FXML
    private void handleGoBack() {
        Stage stage = (Stage) notify.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}