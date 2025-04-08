package controller.Superadmin;

import javafx.fxml.FXML;
import javafx.scene.Node;
import utils.Navigator;

public class SuperadminNavbarController {

    @FXML
    private void goToDashboard(javafx.event.ActionEvent event) {
        Navigator.navigateTo("superadmin_dashboard.fxml", (Node) event.getSource());
    }

    @FXML
    private void goToReservations(javafx.event.ActionEvent event) {
        Navigator.navigateTo("superadmin_reservations.fxml", (Node) event.getSource());
    }

    @FXML
    private void goToWorkspaces(javafx.event.ActionEvent event) {
        Navigator.navigateTo("superadmin_workspaces.fxml", (Node) event.getSource());
    }

    @FXML
    private void goToAuditLog(javafx.event.ActionEvent event) {
        Navigator.navigateTo("audit_log.fxml", (Node) event.getSource());
    }

    @FXML
    private void goToUserCreation(javafx.event.ActionEvent event) {
        Navigator.navigateTo("create_users.fxml", (Node) event.getSource());
    }
}
