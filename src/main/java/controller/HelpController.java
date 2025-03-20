package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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

    private String helpSection = "default";
    private final Map<String, String> questionsAndAnswers = new HashMap<>();

    @FXML
    private void initialize() {
        TranslationUtils.setupLanguageSelector(languageSelector, this::updateLanguage);
        updateLanguage();
    }

    public void setHelpSection(String section) {
        if (section != null && !section.isEmpty()) {
            this.helpSection = section;
        }
        updateLanguage();
    }


    private void updateLanguage() {
        ResourceBundle bundle = TranslationUtils.getBundle();
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

            default:
                helpTitle.setText(bundle.getString("help.default.title"));
        }

        questionList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            answerText.setText(questionsAndAnswers.getOrDefault(newValue, ""));
        });
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
