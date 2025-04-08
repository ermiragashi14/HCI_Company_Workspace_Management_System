package controller.Superadmin;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import repository.SuperadminRepository;
import service.SessionManager;

public class SuperadminDashboardController {

    @FXML private Label adminCountLabel;
    @FXML private Label staffCountLabel;
    @FXML private Label workspaceCountLabel;
    @FXML private Label reservationCountLabel;
    @FXML private LineChart<String, Number> reservationTrendsChart;

    private final SuperadminRepository repo = new SuperadminRepository();
    private final int companyId = SessionManager.getInstance().getLoggedInCompanyId();

    @FXML
    public void initialize() {
        adminCountLabel.setText(String.valueOf(repo.countAdmins(companyId)));
        staffCountLabel.setText(String.valueOf(repo.countStaff(companyId)));
        workspaceCountLabel.setText(String.valueOf(repo.countWorkspaces(companyId)));
        reservationCountLabel.setText(String.valueOf(repo.countActiveReservations(companyId)));

        loadReservationTrendsChart();
    }

    private void loadReservationTrendsChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        repo.getMonthlyReservationTrends(companyId).forEach((month, count) ->
                series.getData().add(new XYChart.Data<>(month, count))
        );
        reservationTrendsChart.getData().add(series);
    }
}
