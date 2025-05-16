package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import service.SessionManager;
import utils.Navigator;
import utils.TranslationUtils;

public class RibbonController {

    @FXML private ComboBox<String> languageSelector;
    @FXML private Button goToLogout;

    @FXML
    public void initialize() {
        TranslationUtils.setupLanguageSelector(languageSelector);
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
}
