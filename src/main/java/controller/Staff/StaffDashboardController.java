package controller.Staff;

import dto.RecentReservationsDTO;
import dto.UpcomingReservationDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import repository.StaffRepository;
import service.SessionManager;
import utils.KeyboardNavigator;
import utils.TranslationManager;

import java.util.List;
import java.util.ResourceBundle;

public class StaffDashboardController {

    @FXML private Label activeReservationsLabel;
    @FXML private Label totalReservationsLabel;
    @FXML private Label activeReservationsText;
    @FXML private Label totalReservationsText;
    @FXML private Label recentReservationsLabel;
    @FXML private Label upcomingReservationsLabel;

    @FXML private TableView<RecentReservationsDTO> recentReservationsTable;
    @FXML private TableColumn<RecentReservationsDTO, String> dateColumn;
    @FXML private TableColumn<RecentReservationsDTO, String> timeColumn;
    @FXML private TableColumn<RecentReservationsDTO, String> workspaceColumn;
    @FXML private TableColumn<RecentReservationsDTO, String> statusColumn;

    @FXML private VBox navbarContainer;
    @FXML private VBox ribbonContainer;
    @FXML private VBox mainContentContainer;

    @FXML private ListView<String> upcomingReservationList;

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
        loadUpcomingReminders(userId);

        updateLanguage();
        KeyboardNavigator.enableFullNavigation(false, navbarContainer, ribbonContainer, mainContentContainer);
    }

    private void updateLanguage() {
        bundle = TranslationManager.getBundle();

        if (activeReservationsText != null)
            activeReservationsText.setText(bundle.getString("staff.dashboard.activereservations"));
        if (totalReservationsText != null)
            totalReservationsText.setText(bundle.getString("staff.dashboard.totalreservations"));
        if (recentReservationsLabel != null)
            recentReservationsLabel.setText(bundle.getString("staff.dashboard.recentreservations"));
        if (upcomingReservationsLabel != null)
            upcomingReservationsLabel.setText(bundle.getString("staff.dashboard.upcomingreservations"));

        if (dateColumn != null)
            dateColumn.setText(bundle.getString("staff.dashboard.table.date"));
        if (timeColumn != null)
            timeColumn.setText(bundle.getString("staff.dashboard.table.time"));
        if (workspaceColumn != null)
            workspaceColumn.setText(bundle.getString("staff.dashboard.table.workspace"));
        if (statusColumn != null)
            statusColumn.setText(bundle.getString("staff.dashboard.table.status"));
    }

    private void setupRecentReservationsTable(int userId) {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        workspaceColumn.setCellValueFactory(new PropertyValueFactory<>("workspace"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        List<RecentReservationsDTO> recentReservations = staffRepository.getRecentReservations(userId);
        recentReservationsTable.setItems(FXCollections.observableArrayList(recentReservations));
    }

    private void loadUpcomingReminders(int userId) {
        List<UpcomingReservationDTO> reminders = staffRepository.getUpcomingReservationsWithin24Hours(userId);
        List<String> displayList = reminders.stream()
                .map(r -> r.getDate() + " | " + r.getWorkspace() + " | " + r.getTime())

                .toList();
        upcomingReservationList.setItems(FXCollections.observableArrayList(displayList));
    }
}
