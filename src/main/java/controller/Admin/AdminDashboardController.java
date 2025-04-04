package controller.Admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import service.AdminService;

import java.io.IOException;

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

    private final AdminService adminService = new AdminService();

    public void initialize() {
        loadDashboardStats();
        setupCharts();
        loadNavbar();
    }

    private void loadDashboardStats() {
        totalUsersLabel.setText(String.valueOf(adminService.getTotalUsers()));
        totalWorkspacesLabel.setText(String.valueOf(adminService.getTotalWorkspaces()));
        totalActiveReservationsLabel.setText(String.valueOf(adminService.getTotalActiveReservations()));
    }

    private void setupCharts() {
        workspaceUsageChart.getData().add(new PieChart.Data("Occupied", 60));
        workspaceUsageChart.getData().add(new PieChart.Data("Available", 40));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Reservations Trend");
        series.getData().add(new XYChart.Data<>("Jan", 20));
        series.getData().add(new XYChart.Data<>("Feb", 25));
        series.getData().add(new XYChart.Data<>("Mar", 30));

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
