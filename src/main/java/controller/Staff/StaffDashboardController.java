package controller.Staff;

import dto.RecentReservationsDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import repository.StaffRepository;
import service.SessionManager;
import utils.TranslationManager;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class StaffDashboardController {

    @FXML private Label activeReservationsLabel;
    @FXML private Label totalReservationsLabel;
    @FXML private Label activeReservationsText;
    @FXML private Label totalReservationsText;
    @FXML private Label recentReservationsLabel;

    @FXML private TableView<RecentReservationsDTO> recentReservationsTable;
    @FXML private TableColumn<RecentReservationsDTO, String> dateColumn;
    @FXML private TableColumn<RecentReservationsDTO, String> timeColumn;
    @FXML private TableColumn<RecentReservationsDTO, String> workspaceColumn;
    @FXML private NumberAxis yAxis;

    @FXML private BarChart<String, Number> workspaceUsageBarChart;

    private final StaffRepository staffRepository = new StaffRepository();
    private ResourceBundle bundle;

    @FXML
    public void initialize() {
        bundle = TranslationManager.getBundle();
        TranslationManager.addListener(this::updateLanguage);

        int userId = SessionManager.getInstance().getLoggedInUserId();

        activeReservationsLabel.setText(String.valueOf(staffRepository.countActiveReservations(userId)));
        totalReservationsLabel.setText(String.valueOf(staffRepository.countTotalReservations(userId)));

        setupRecentReservationsTable(userId);
        setupWorkspaceUsageChart(userId);

        yAxis.setLowerBound(0);
        yAxis.setUpperBound(50);
        yAxis.setTickUnit(5);

        updateLanguage();
    }

    private void updateLanguage() {
        bundle = TranslationManager.getBundle();

        if (activeReservationsText != null) activeReservationsText.setText(bundle.getString("staff.dashboard.activereservations"));
        if (totalReservationsText != null) totalReservationsText.setText(bundle.getString("staff.dashboard.totalreservations"));
        if (recentReservationsLabel != null) recentReservationsLabel.setText(bundle.getString("staff.dashboard.recentreservations"));

        if (dateColumn != null) dateColumn.setText(bundle.getString("staff.dashboard.table.date"));
        if (timeColumn != null) timeColumn.setText(bundle.getString("staff.dashboard.table.time"));
        if (workspaceColumn != null) workspaceColumn.setText(bundle.getString("staff.dashboard.table.workspace"));

        if (workspaceUsageBarChart != null) {
            workspaceUsageBarChart.setTitle(bundle.getString("staff.dashboard.chart.usage"));
            yAxis.setLabel(bundle.getString("staff.dashboard.chart.usage"));
        }
    }

    private void setupRecentReservationsTable(int userId) {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        workspaceColumn.setCellValueFactory(new PropertyValueFactory<>("workspace"));

        List<RecentReservationsDTO> recentReservations = staffRepository.getRecentReservations(userId);
        recentReservationsTable.setItems(FXCollections.observableArrayList(recentReservations));
    }

    private void setupWorkspaceUsageChart(int userId) {
        workspaceUsageBarChart.getData().clear();
        Map<String, Integer> usageStats = staffRepository.getWorkspaceUsageStats(userId);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(bundle.getString("staff.dashboard.chart.usage"));

        for (Map.Entry<String, Integer> entry : usageStats.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        workspaceUsageBarChart.getData().add(series);
    }
}
