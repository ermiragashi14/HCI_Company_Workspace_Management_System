package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import service.SessionManager;
import utils.Navigator;
import utils.TranslationUtils;

public class RibbonController {

    @FXML private ComboBox<String> languageSelector;
    @FXML private Button goToSettings;
    @FXML private Button goToNotifications;
    @FXML private Button goToHelp;
    @FXML private Button goToLogout;

    @FXML
    public void initialize() {
        TranslationUtils.setupLanguageSelector(languageSelector);
    }

    @FXML
    public void goToSettings() {Navigator.navigateTo("settings.fxml", goToSettings);}

    @FXML
    public void goToNotifications() {Navigator.navigateTo("notifications.fxml", goToNotifications);}

    @FXML
    public void goToHelp() {Navigator.navigateTo("help.fxml", goToHelp);};

    @FXML
    public void logout() {
        SessionManager.getInstance().clearSession();
        Navigator.navigateTo("login.fxml", goToLogout);
    }
}
