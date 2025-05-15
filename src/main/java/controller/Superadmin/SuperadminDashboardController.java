package controller.Superadmin;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import repository.SuperadminRepository;
import service.SessionManager;
import utils.KeyboardNavigator;
import utils.TranslationManager;

import java.util.ResourceBundle;
import java.util.Map;

public class SuperadminDashboardController {

    @FXML private Label adminCountLabel;
    @FXML private Label staffCountLabel;
    @FXML private Label workspaceCountLabel;
    @FXML private Label reservationCountLabel;
    @FXML private Label adminLabel;
    @FXML private Label staffLabel;
    @FXML private Label workspaceLabel;
    @FXML private Label reservationLabel;
    @FXML private LineChart<String, Number> reservationTrendsChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private VBox navbarContainer;
    @FXML private VBox ribbonContainer;
    @FXML private VBox mainContentContainer;


    private final SuperadminRepository repo = new SuperadminRepository();
    private final int companyId = SessionManager.getInstance().getLoggedInCompanyId();
    ResourceBundle bundle;

    @FXML
    public void initialize() {
        loadData();
        loadReservationTrendsChart();
        updateLanguage();
        TranslationManager.addListener(this::updateLanguage);
        KeyboardNavigator.enableFullNavigation(navbarContainer, ribbonContainer, mainContentContainer);
    }

    private void updateLanguage() {
        bundle = TranslationManager.getBundle();
        adminLabel.setText(bundle.getString("super.dashboard.admins"));
        staffLabel.setText(bundle.getString("super.dashboard.staff"));
        workspaceLabel.setText(bundle.getString("super.dashboard.workspaces"));
        reservationLabel.setText(bundle.getString("super.dashboard.reservations"));
        xAxis.setLabel(bundle.getString("super.chart.months"));
        yAxis.setLabel(bundle.getString("super.chart.reservations"));
    }

    private void loadData() {
        adminCountLabel.setText(String.valueOf(repo.countAdmins(companyId)));
        staffCountLabel.setText(String.valueOf(repo.countStaff(companyId)));
        workspaceCountLabel.setText(String.valueOf(repo.countWorkspaces(companyId)));
        reservationCountLabel.setText(String.valueOf(repo.countActiveReservations(companyId)));
    }

    private void loadReservationTrendsChart() {
        reservationTrendsChart.getData().clear();
        yAxis.setTickUnit(1);
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(10);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Map<String, Integer> trends = repo.getMonthlyReservationTrends(companyId);
        trends.forEach((month, count) -> {
            series.getData().add(new XYChart.Data<>(month, count));
        });

        reservationTrendsChart.getData().add(series);
    }
}
