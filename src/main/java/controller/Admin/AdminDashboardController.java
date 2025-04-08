package controller.Admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import repository.AdminRepository;

import java.io.IOException;
import java.util.Map;

public class AdminDashboardController {
    @FXML
    private VBox navbarContainer;

    @FXML
    private Label totalUsersLabel;

    @FXML
    private Label totalWorkspacesLabel;

    @FXML
    private Label totalActiveReservationsLabel;

    @FXML
    private PieChart workspaceUsageChart;

    @FXML
    private LineChart<String, Number> reservationTrendsChart;

    private final AdminRepository adminRepository = new AdminRepository();

    public void initialize() {
        loadDashboardStats();
        setupCharts();
        loadNavbar();
    }

    private void loadDashboardStats() {
        totalUsersLabel.setText(String.valueOf(adminRepository.countTotalUsers()));
        totalWorkspacesLabel.setText(String.valueOf(adminRepository.countTotalWorkspaces()));
        totalActiveReservationsLabel.setText(String.valueOf(adminRepository.countTotalActiveReservations()));
    }

    private void setupCharts() {
        Map<String, Integer> usageStats = adminRepository.getWorkspaceUsageStats();
        for (Map.Entry<String, Integer> entry : usageStats.entrySet()) {
            workspaceUsageChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Reservations Trend");

        Map<String, Integer> trends = adminRepository.getMonthlyReservationTrends();
        for (Map.Entry<String, Integer> entry : trends.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        reservationTrendsChart.getData().add(series);
    }

    private void loadNavbar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/admin_navbar.fxml"));
            Parent navbar = loader.load();
            navbarContainer.getChildren().add(navbar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
