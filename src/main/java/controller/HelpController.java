package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.SessionManager;
import utils.KeyboardNavigator;
import utils.Navigator;
import utils.TranslationManager;
import utils.TranslationUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class HelpController {

    @FXML private Label helpTitle;
    @FXML private ListView<String> questionList;
    @FXML private TextArea answerText;
    @FXML private VBox helpBox;
    @FXML private ComboBox<String> languageSelector;
    @FXML private Button closeButton;
    @FXML private AnchorPane dialogRoot;

    private String helpSection = "default";
    private final Map<String, String> questionsAndAnswers = new HashMap<>();
    ResourceBundle bundle;

    @FXML
    private void initialize() {

        TranslationUtils.setupLanguageSelector(languageSelector);
        updateLanguage();
        TranslationManager.addListener(this::updateLanguage);
        KeyboardNavigator.enableNavigation(dialogRoot);
    }

    private void updateLanguage() {

        bundle = TranslationManager.getBundle();
        helpTitle.setText(bundle.getString("help.title"));
        closeButton.setText(bundle.getString("help.button.close"));

        questionList.getItems().clear();
        questionsAndAnswers.clear();

        switch (helpSection) {
            case "login":
                addHelpContent(bundle, "help.login.q1", "help.login.a1");
                addHelpContent(bundle, "help.login.q2", "help.login.a2");
                addHelpContent(bundle, "help.login.q3", "help.login.a3");
                break;

            case "register":
                addHelpContent(bundle, "help.register.q1", "help.register.a1");
                addHelpContent(bundle, "help.register.q2", "help.register.a2");
                addHelpContent(bundle, "help.register.q3", "help.register.a3");
                break;

            case "forgetpassword":
                addHelpContent(bundle, "help.forgetpassword.q1", "help.forgetpassword.a1");
                addHelpContent(bundle, "help.forgetpassword.q2", "help.forgetpassword.a2");
                addHelpContent(bundle, "help.forgetpassword.q3", "help.forgetpassword.a3");
                break;

            case "staff":
                addHelpContent(bundle, "help.staff.q1", "help.staff.a1");
                addHelpContent(bundle, "help.staff.q2", "help.staff.a2");
                addHelpContent(bundle, "help.staff.q3", "help.staff.a3");
                addHelpContent(bundle, "help.staff.q4", "help.staff.a4");
                addHelpContent(bundle, "help.staff.q5", "help.staff.a5");
                break;

            case "admin":
                addHelpContent(bundle, "help.admin.q1", "help.admin.a1");
                addHelpContent(bundle, "help.admin.q2", "help.admin.a2");
                addHelpContent(bundle, "help.admin.q3", "help.admin.a3");
                addHelpContent(bundle, "help.admin.q4", "help.admin.a4");
                break;

            case "superadmin":
                addHelpContent(bundle, "help.superadmin.q1", "help.superadmin.a1");
                addHelpContent(bundle, "help.superadmin.q2", "help.superadmin.a2");
                addHelpContent(bundle, "help.superadmin.q3", "help.superadmin.a3");
                addHelpContent(bundle, "help.superadmin.q4", "help.superadmin.a4");
                break;

            default:
                helpTitle.setText(bundle.getString("help.default.title"));
        }

        questionList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            answerText.setText(questionsAndAnswers.getOrDefault(newValue, ""));
        });
    }

    public void setHelpSection(String section) {
        if (section != null && !section.isEmpty()) {
            this.helpSection = section;
        }
        updateLanguage();
    }

    private void addHelpContent(ResourceBundle bundle, String questionKey, String answerKey) {

        if (bundle.containsKey(questionKey) && bundle.containsKey(answerKey)) {
            String question = bundle.getString(questionKey);
            String answer = bundle.getString(answerKey);
            questionList.getItems().add(question);
            questionsAndAnswers.put(question, answer);
        }
    }

    @FXML
    private void closeHelp() {
        Stage stage = (Stage) helpBox.getScene().getWindow();
        stage.close();
    }
}
