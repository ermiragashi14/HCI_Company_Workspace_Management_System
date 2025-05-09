package controller.Staff;

import dto.RecentReservationsDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import repository.StaffRepository;
import service.SessionManager;

import java.util.List;
import java.util.Map;

public class StaffDashboardController {

    @FXML private Label activeReservationsLabel;
    @FXML private Label totalReservationsLabel;

    @FXML private TableView<RecentReservationsDTO> recentReservationsTable;
    @FXML private TableColumn<RecentReservationsDTO, String> dateColumn;
    @FXML private TableColumn<RecentReservationsDTO, String> timeColumn;
    @FXML private TableColumn<RecentReservationsDTO, String> workspaceColumn;

    @FXML private PieChart workspaceUsageChart;

    private final StaffRepository staffRepository = new StaffRepository();

    @FXML
    public void initialize() {
        int userId = SessionManager.getInstance().getLoggedInUserId();

        activeReservationsLabel.setText(String.valueOf(staffRepository.countActiveReservations(userId)));
        totalReservationsLabel.setText(String.valueOf(staffRepository.countTotalReservations(userId)));

        setupRecentReservationsTable(userId);
        setupWorkspaceUsageChart(userId);
    }

    private void setupRecentReservationsTable(int userId) {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        workspaceColumn.setCellValueFactory(new PropertyValueFactory<>("workspace"));

        List<RecentReservationsDTO> recentReservations = staffRepository.getRecentReservations(userId);
        recentReservationsTable.setItems(FXCollections.observableArrayList(recentReservations));
    }

    private void setupWorkspaceUsageChart(int userId) {
        workspaceUsageChart.getData().clear();
        Map<String, Integer> usageStats = staffRepository.getWorkspaceUsageStats(userId);

        for (Map.Entry<String, Integer> entry : usageStats.entrySet()) {
            workspaceUsageChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
    }
}
