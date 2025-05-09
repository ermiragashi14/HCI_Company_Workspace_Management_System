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
import repository.ReportsAnalyticsRepository;
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
    @FXML private Label topUsersLabel;

    @FXML private PieChart statusPieChart;
    @FXML private LineChart<String, Number> monthlyLineChart;

    @FXML private TableView<UserStatDTO> topUsersTable;
    @FXML private TableColumn<UserStatDTO, String> fullNameColumn;
    @FXML private TableColumn<UserStatDTO, Integer> reservationCountColumn;

    private final ReportsAnalyticsRepository repository = new ReportsAnalyticsRepository();

    @FXML
    public void initialize() {
        loadTotalReservations();
        loadActiveReservations();
        loadReservationStatusChart();
        loadMonthlyTrendsChart();
        setupTopUsersTable();
        loadTopUsers();
        translate();
        TranslationManager.addListener(this::translate);
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
            statusPieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
    }

    private void loadMonthlyTrendsChart() {
        Map<String, Integer> trends = repository.getReservationsPerMonth();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        ResourceBundle bundle = TranslationManager.getBundle();
        series.setName(bundle.getString("admin.chart.reservations"));

        for (Map.Entry<String, Integer> entry : trends.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        monthlyLineChart.getData().clear();
        monthlyLineChart.getData().add(series);
    }

    private void setupTopUsersTable() {
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        reservationCountColumn.setCellValueFactory(new PropertyValueFactory<>("reservationCount"));
    }

    private void loadTopUsers() {
        List<UserStatDTO> topUsers = repository.getMostActiveUsers();
        topUsersTable.setItems(FXCollections.observableArrayList(topUsers));
    }

    private void translate() {
        ResourceBundle bundle = TranslationManager.getBundle();
        if (totalReservationsText != null) totalReservationsText.setText(bundle.getString("admin.dashboard.totalreservations"));
        if (activeReservationsText != null) activeReservationsText.setText(bundle.getString("admin.dashboard.totalreservations"));
        if (chartTitle != null) chartTitle.setText(bundle.getString("admin.chart.reservations"));
        if (topUsersLabel != null) topUsersLabel.setText(bundle.getString("admin.chart.topusers"));
        if (fullNameColumn != null) fullNameColumn.setText(bundle.getString("table.column.name"));
        if (reservationCountColumn != null) reservationCountColumn.setText(bundle.getString("table.column.count"));
    }
}
