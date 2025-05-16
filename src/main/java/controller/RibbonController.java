package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import service.NotificationService;
import service.SessionManager;
import utils.Navigator;
import utils.TranslationManager;
import utils.TranslationUtils;

import java.util.ResourceBundle;

public class RibbonController {

    @FXML private ComboBox<String> languageSelector;
    @FXML private Button goToLogout;

    private final NotificationService notificationService = new NotificationService();
    private boolean notificationAlertShown = false;

    @FXML
    public void initialize() {
        TranslationUtils.setupLanguageSelector(languageSelector);
        Platform.runLater(() -> {
            checkForNewNotifications();
        });
    }

    @FXML
    public void goToSettings() {
        Navigator.openPopupWindow("settings.fxml", "Settings");}

    @FXML
    public void goToNotifications() {
        Navigator.openPopupWindow("notifications.fxml", "Notifications");
    }

    @FXML
    private void openHelp() {
        String role = SessionManager.getInstance().getLoggedInUserRole();

        switch (role.toUpperCase()) {
            case "SUPER_ADMIN" -> Navigator.openHelpWindow("superadmin");
            case "ADMIN" -> Navigator.openHelpWindow("admin");
            case "STAFF" -> Navigator.openHelpWindow("staff");
            default -> Navigator.openHelpWindow("default");
        }
    }


    @FXML
    public void logout() {
        SessionManager.getInstance().clearSession();
        Navigator.navigateTo("login.fxml", goToLogout);
    }

    public void checkForNewNotifications() {
        SessionManager session = SessionManager.getInstance();

        if (session.isNotificationAlertShown()) {
            return;
        }

        int userId = session.getLoggedInUserId();
        int unreadCount = notificationService.countUnreadNotifications(userId);

        if (unreadCount > 0) {
            showNotificationAlert(unreadCount);
            session.setNotificationAlertShown(true);
        }
    }

    private void showNotificationAlert(int count) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        ResourceBundle bundle = TranslationManager.getBundle();

        alert.setTitle(bundle.getString("notification.alert.title"));
        alert.setHeaderText(null);

        String message;
        if (count == 1) {
            message = bundle.getString("notification.alert.message.singular").replace("{count}", String.valueOf(count));
        } else {
            message = bundle.getString("notification.alert.message.plural").replace("{count}", String.valueOf(count));
        }

        alert.setContentText(message);
        alert.showAndWait();
    }


}
