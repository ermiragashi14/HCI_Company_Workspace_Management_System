package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.io.IOException;

public class AdminNavbarController {

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
    public void goToDashboard(ActionEvent event) {
        switchScene(event, "admin_dashboard.fxml");
    }

    @FXML
    public void goToUserManagement(ActionEvent event) {
        switchScene(event, "user_management.fxml");
    }

    @FXML
    public void goToOfficeManagement(ActionEvent event) {
        switchScene(event, "office_management.fxml");
    }

    @FXML
    public void goToReservations(ActionEvent event) {
        switchScene(event, "reservations.fxml");
    }

    @FXML
    public void goToReports(ActionEvent event) {
        switchScene(event, "reports.fxml");
    }
}
