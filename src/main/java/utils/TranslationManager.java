package utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class TranslationManager {
    private static Locale currentLocale = new Locale("en");
    private static ResourceBundle bundle = ResourceBundle.getBundle("translations.lang", currentLocale);

    public static void setLanguage(String languageCode) {
        currentLocale = new Locale(languageCode);
        bundle = ResourceBundle.getBundle("translations.lang", currentLocale);
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static String getCurrentLanguage() {
        return currentLocale.getLanguage();
    }
}
