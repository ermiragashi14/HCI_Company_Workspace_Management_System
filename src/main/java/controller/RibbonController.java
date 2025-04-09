package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.event.ActionEvent;
import service.SessionManager;
import utils.TranslationManager;

import java.io.IOException;

public class RibbonController {

    @FXML
    private ComboBox<String> languageSelector;

    @FXML
    public void initialize() {
        languageSelector.getItems().addAll("English", "Shqip");
        String currentLang = TranslationManager.getCurrentLanguage();
        languageSelector.setValue(currentLang.equals("sq") ? "Shqip" : "English");

        languageSelector.setOnAction(event -> {
            String selected = languageSelector.getValue();
            if ("English".equals(selected)) {
                TranslationManager.setLanguage("en");
            } else if ("Shqip".equals(selected)) {
                TranslationManager.setLanguage("sq");
            }
        });
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
