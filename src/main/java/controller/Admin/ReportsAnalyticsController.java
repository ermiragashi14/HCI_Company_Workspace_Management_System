package controller.Admin;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import dto.UserStatDTO;
import javafx.scene.layout.VBox;
import repository.ReportsAnalyticsRepository;
import utils.KeyboardNavigator;
import utils.TranslationManager;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ReportsAnalyticsController {

    @FXML private Label totalReservationsLabel;
    @FXML private Label activeReservationsLabel;
    @FXML private Label totalReservationsText;
    @FXML private Label activeReservationsText;
    @FXML private Label chartTitle;
    @FXML private Label statusPieChartTitle;

    @FXML private PieChart statusPieChart;
    @FXML private LineChart<String, Number> monthlyLineChart;
    @FXML private javafx.scene.chart.CategoryAxis xAxis;
    @FXML private javafx.scene.chart.NumberAxis yAxis;

    @FXML private TableView<UserStatDTO> topUsersTable;
    @FXML private TableColumn<UserStatDTO, String> fullNameColumn;
    @FXML private TableColumn<UserStatDTO, Integer> reservationCountColumn;
    @FXML private VBox navbarContainer;
    @FXML private VBox ribbonContainer;
    @FXML private VBox mainContentContainer;


    private final ReportsAnalyticsRepository repository = new ReportsAnalyticsRepository();
    ResourceBundle bundle;

    @FXML
    public void initialize() {
        updateLanguage();
        TranslationManager.addListener(this::updateLanguage);
        loadTotalReservations();
        loadActiveReservations();
        loadReservationStatusChart();
        loadMonthlyTrendsChart();
        setupTopUsersTable();
        loadTopUsers();
        KeyboardNavigator.enableFullNavigation(false, navbarContainer, ribbonContainer, mainContentContainer);
    }

    private void updateLanguage() {
        bundle = TranslationManager.getBundle();

        if (totalReservationsText != null)
            totalReservationsText.setText(bundle.getString("admin.dashboard.totalreservations"));
        if (activeReservationsText != null)
            activeReservationsText.setText(bundle.getString("admin.dashboard.activereservations"));
        if (chartTitle != null)
            chartTitle.setText(bundle.getString("admin.chart.reservations"));
        if (fullNameColumn != null)
            fullNameColumn.setText(bundle.getString("table.column.name"));
        if (reservationCountColumn != null)
            reservationCountColumn.setText(bundle.getString("table.column.count"));
        if (xAxis != null)
            xAxis.setLabel(bundle.getString("admin.chart.months"));
        if (yAxis != null)
            yAxis.setLabel(bundle.getString("admin.chart.reservations"));
        if (statusPieChartTitle != null)
            statusPieChartTitle.setText(bundle.getString("admin.chart.statusoverview")); // ADDED
    }

    private void loadTotalReservations() {
        int total = repository.getTotalReservations();
        totalReservationsLabel.setText(String.valueOf(total));
    }

    private void loadActiveReservations() {
        int active = repository.getActiveReservations();
        activeReservationsLabel.setText(String.valueOf(active));
    }

    private void loadReservationStatusChart() {
        Map<String, Integer> statusMap = repository.getReservationsByStatus();
        statusPieChart.getData().clear();
        for (Map.Entry<String, Integer> entry : statusMap.entrySet()) {
            String translatedLabel = translateStatus(entry.getKey());
            statusPieChart.getData().add(new PieChart.Data(translatedLabel, entry.getValue()));
        }
    }

    private void loadMonthlyTrendsChart() {
        Map<String, Integer> trends = repository.getReservationsPerMonth();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(bundle.getString("admin.chart.reservations"));

        int maxValue = 0;
        for (Map.Entry<String, Integer> entry : trends.entrySet()) {
            int value = entry.getValue();
            if (value > maxValue) maxValue = value;
            series.getData().add(new XYChart.Data<>(entry.getKey(), value));
        }

        monthlyLineChart.getData().clear();
        monthlyLineChart.getData().add(series);


        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setTickUnit(1);
        yAxis.setUpperBound(7);
    }


    private void setupTopUsersTable() {
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        reservationCountColumn.setCellValueFactory(new PropertyValueFactory<>("reservationCount"));
    }

    private void loadTopUsers() {
        List<UserStatDTO> topUsers = repository.getMostActiveUsers();
        topUsersTable.setItems(FXCollections.observableArrayList(topUsers));
    }

    private String translateStatus(String status) {
        return switch (status.toUpperCase()) {
            case "CONFIRMED" -> bundle.getString("status.confirmed");
            case "CANCELED" -> bundle.getString("status.canceled");
            case "EXPIRED" -> bundle.getString("status.expired");
            default -> status;
        };
    }
}
