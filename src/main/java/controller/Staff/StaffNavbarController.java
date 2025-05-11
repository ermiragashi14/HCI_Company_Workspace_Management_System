package controller.Staff;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Button;
import utils.TranslationManager;

import java.io.IOException;
import java.util.ResourceBundle;

public class StaffNavbarController {

    @FXML private Button dashboardButton;
    @FXML private Button availableWorkspacesButton;
    @FXML private Button reservationsButton;
    @FXML private Button reportsButton;

    ResourceBundle bundle;

    @FXML
    public void initialize() {

        updateLanguage();
        TranslationManager.addListener(this::updateLanguage);
    }

    private void updateLanguage() {

        bundle = TranslationManager.getBundle();
        dashboardButton.setText("🏠 " + bundle.getString("staff.nav.dashboard"));
        availableWorkspacesButton.setText("🏢 " + bundle.getString("staff.nav.available"));
        reservationsButton.setText("📅 " + bundle.getString("staff.nav.reserve"));
        reportsButton.setText("📋 " + bundle.getString("staff.nav.myreservations"));
    }

    private void switchScene(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/Staff/" + fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToDashboard(ActionEvent event) {
        switchScene(event, "staff_dashboard.fxml");
    }

    @FXML
    private void goToAvailableWorkspaces(ActionEvent event) {
        switchScene(event, "available_workspaces.fxml");
    }

    @FXML
    private void goToMakeReservation(ActionEvent event) {
        switchScene(event, "make_reservation.fxml");
    }

    @FXML
    private void goToMyReservations(ActionEvent event) {
        switchScene(event, "my_reservations.fxml");
    }
}
