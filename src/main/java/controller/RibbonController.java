package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.event.ActionEvent;
import service.SessionManager;
import utils.TranslationManager;
import utils.TranslationUtils;

import java.io.IOException;
import java.util.ResourceBundle;

public class RibbonController {

    @FXML
    private ComboBox<String> languageSelector;

    @FXML
    public void initialize() {

        TranslationUtils.setupLanguageSelector(languageSelector);
    }

    private void switchScene(ActionEvent event, String fxmlFile) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/" + fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @FXML
    public void goToSettings(ActionEvent event) {
        switchScene(event, "settings.fxml");
    }

    @FXML
    public void goToNotifications(ActionEvent event) {
        switchScene(event, "notifications.fxml");
    }

    @FXML
    public void goToHelp(ActionEvent event) {
        switchScene(event, "help.fxml");
    }

    @FXML
    public void logout(ActionEvent event) {

        SessionManager.getInstance().clearSession();
        switchScene(event, "login.fxml");
    }

}
