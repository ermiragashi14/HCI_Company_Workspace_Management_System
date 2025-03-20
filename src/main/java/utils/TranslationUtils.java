package utils;

import javafx.scene.control.ComboBox;
import java.util.ResourceBundle;

public class TranslationUtils {

    public static void setupLanguageSelector(ComboBox<String> languageSelector, Runnable updateUI) {
        if (languageSelector != null) {
            languageSelector.getItems().addAll("English", "Shqip");
            languageSelector.setValue(TranslationManager.getCurrentLanguage().equals("sq") ? "Shqip" : "English");

            languageSelector.setOnAction(event -> {
                changeLanguage(languageSelector);
                updateUI.run();
            });
        }
    }

    public static void changeLanguage(ComboBox<String> languageSelector) {
        if (languageSelector != null) {
            String selectedLang = languageSelector.getValue();
            if (selectedLang.equals("English")) {
                TranslationManager.setLanguage("en");
            } else if (selectedLang.equals("Shqip")) {
                TranslationManager.setLanguage("sq");
            }
        }
    }

    public static ResourceBundle getBundle() {
        return TranslationManager.getBundle();
    }
}
