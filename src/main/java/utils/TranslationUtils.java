package utils;

import javafx.scene.control.ComboBox;

public class TranslationUtils {

    public static void setupLanguageSelector(ComboBox<String> languageSelector) {

        if (languageSelector != null) {
            languageSelector.getItems().setAll("English", "Shqip");

            String currentLang = TranslationManager.getCurrentLanguage();
            languageSelector.setValue(currentLang.equals("sq") ? "Shqip" : "English");

            languageSelector.setOnAction(event -> {
                String selectedLang = languageSelector.getValue();
                if ("English".equals(selectedLang)) {
                    TranslationManager.setLanguage("en");
                } else if ("Shqip".equals(selectedLang)) {
                    TranslationManager.setLanguage("sq");
                }
            });
        }
    }
}

