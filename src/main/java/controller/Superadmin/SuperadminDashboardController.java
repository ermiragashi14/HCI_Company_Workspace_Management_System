package controller.Superadmin;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SuperadminDashboardController {

    @FXML
    private Label companyCountLabel;

    @FXML
    private Label adminCountLabel;

    @FXML
    private Label workspaceCountLabel;

    @FXML
    private Label reservationCountLabel;

    @FXML
    public void initialize() {
        //i marrim tani shembujt prej databazes
        companyCountLabel.setText("12");
        adminCountLabel.setText("36");
        workspaceCountLabel.setText("92");
        reservationCountLabel.setText("317");
    }
}
