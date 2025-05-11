package utils;

import java.util.*;

public class TranslationManager {

    private static Locale currentLocale = new Locale("en");
    private static ResourceBundle bundle = ResourceBundle.getBundle("translations.lang", currentLocale);

    private static final List<Runnable> listeners = new ArrayList<>();

    public static void setLanguage(String languageCode) {
        currentLocale = new Locale(languageCode);
        bundle = ResourceBundle.getBundle("translations.lang", currentLocale);
        notifyListeners();
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static String getCurrentLanguage() {
        return currentLocale.getLanguage();
    }

    public static void addListener(Runnable listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    private static void notifyListeners() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }

    public static void removeListener(Runnable listener) {
        listeners.remove(listener);
    }

}

