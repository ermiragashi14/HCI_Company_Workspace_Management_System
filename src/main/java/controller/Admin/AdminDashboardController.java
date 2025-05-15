package controller.Admin;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import repository.AdminRepository;
import utils.KeyboardNavigator;
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
    @FXML private Label mostUsedChartTitle;

    @FXML private PieChart mostUsedWorkspacesChart;

    @FXML private LineChart<String, Number> reservationTrendsChart;

    @FXML private VBox navbarContainer;
    @FXML private VBox ribbonContainer;
    @FXML private VBox mainContentContainer;


    private final AdminRepository adminRepository = new AdminRepository();
    ResourceBundle bundle;

    @FXML
    public void initialize() {

        loadDashboardStats();
        loadMostUsedWorkspacesChart();
        loadReservationTrendsChart();
        updateLanguage();
        TranslationManager.addListener(this::updateLanguage);
        KeyboardNavigator.enableFullNavigation(navbarContainer, ribbonContainer, mainContentContainer);
    }

    private void updateLanguage() {

        bundle = TranslationManager.getBundle();
        staffLabel.setText(bundle.getString("admin.dashboard.totalusers"));
        workspaceLabel.setText(bundle.getString("admin.dashboard.totalworkspaces"));
        reservationLabel.setText(bundle.getString("admin.dashboard.totalreservations"));
        reservationTrendsChart.getXAxis().setLabel(bundle.getString("admin.chart.months"));
        reservationTrendsChart.getYAxis().setLabel(bundle.getString("admin.chart.reservations"));
        mostUsedChartTitle.setText("PieChart: " + bundle.getString("admin.chart.mostusedworkspaces"));
    }

    private void loadDashboardStats() {

        staffCountLabel.setText(String.valueOf(adminRepository.countTotalUsers()));
        workspaceCountLabel.setText(String.valueOf(adminRepository.countTotalWorkspaces()));
        reservationCountLabel.setText(String.valueOf(adminRepository.countTotalActiveReservations()));
    }

    private void loadMostUsedWorkspacesChart() {
        mostUsedWorkspacesChart.getData().clear();
        Map<String, Integer> usageStats = adminRepository.getMostUsedWorkspaces();

        for (Map.Entry<String, Integer> entry : usageStats.entrySet()) {
            mostUsedWorkspacesChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
    }

    private void loadReservationTrendsChart() {
        reservationTrendsChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Reservations Trend");

        Map<String, Integer> trends = adminRepository.getMonthlyReservationTrends();
        int count = 0;

        int maxReservation = 0;

        for (Map.Entry<String, Integer> entry : trends.entrySet()) {
            if (count >= 7) break; // maksimum 7 muaj
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));

            if (entry.getValue() > maxReservation) {
                maxReservation = entry.getValue();
            }

            count++;
        }

        reservationTrendsChart.getData().add(series);


        if (reservationTrendsChart.getYAxis() instanceof javafx.scene.chart.NumberAxis yAxis) {
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(Math.max(7, maxReservation + 1));
            yAxis.setTickUnit(1);
        }
    }

}
