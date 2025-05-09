package controller.Admin;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import repository.AdminRepository;
import utils.TranslationManager;
import java.util.Map;
import java.util.ResourceBundle;

public class AdminDashboardController {


    @FXML private Label staffCountLabel;
    @FXML private Label workspaceCountLabel;
    @FXML private Label reservationCountLabel;

    @FXML private Label staffLabel;
    @FXML private Label workspaceLabel;
    @FXML private Label reservationLabel;

    @FXML private PieChart workspaceUsageChart;
    @FXML private LineChart<String, Number> reservationTrendsChart;

    private final AdminRepository adminRepository = new AdminRepository();

    @FXML
    public void initialize() {
        loadDashboardStats();
        setupCharts();
        translate();
        TranslationManager.addListener(this::translate);
    }

    private void translate() {
        ResourceBundle bundle = TranslationManager.getBundle();
        staffLabel.setText(bundle.getString("admin.dashboard.totalusers"));
        workspaceLabel.setText(bundle.getString("admin.dashboard.totalworkspaces"));
        reservationLabel.setText(bundle.getString("admin.dashboard.totalreservations"));
        reservationTrendsChart.getXAxis().setLabel(bundle.getString("admin.chart.months"));
        reservationTrendsChart.getYAxis().setLabel(bundle.getString("admin.chart.reservations"));
    }

    private void loadDashboardStats() {
        staffCountLabel.setText(String.valueOf(adminRepository.countTotalUsers()));
        workspaceCountLabel.setText(String.valueOf(adminRepository.countTotalWorkspaces()));
        reservationCountLabel.setText(String.valueOf(adminRepository.countTotalActiveReservations()));
    }

    private void setupCharts() {
        workspaceUsageChart.getData().clear();
        Map<String, Integer> usageStats = adminRepository.getWorkspaceUsageStats();
        for (Map.Entry<String, Integer> entry : usageStats.entrySet()) {
            workspaceUsageChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        reservationTrendsChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Reservations Trend");

        Map<String, Integer> trends = adminRepository.getMonthlyReservationTrends();
        for (Map.Entry<String, Integer> entry : trends.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        reservationTrendsChart.getData().add(series);
    }
}
