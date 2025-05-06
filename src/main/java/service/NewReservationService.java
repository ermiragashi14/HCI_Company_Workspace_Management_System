package service;

import dto.NewReservationDTO;
import repository.NewReservationRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class NewReservationService {
    private final NewReservationRepository repo = new NewReservationRepository();

    public List<NewReservationDTO> getAvailableWorkspaces(LocalDate date, LocalTime start, LocalTime end, int companyId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime selectedStart = LocalDateTime.of(date, start);

        if (selectedStart.isBefore(now)) {
            throw new IllegalArgumentException("Cannot reserve a workspace in the past.");
        }

        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }

        try {
            return repo.getAvailableWorkspaces(date, start, end, companyId);
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public boolean createReservation(int userId, int workspaceId, LocalDate date, LocalTime start, LocalTime end) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime selectedStart = LocalDateTime.of(date, start);

        if (selectedStart.isBefore(now)) {
            throw new IllegalArgumentException("Cannot reserve a workspace in the past.");
        }

        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }

        try {
            boolean isAvailable = repo.isWorkspaceAvailable(workspaceId, date, start, end);
            if (!isAvailable) {
                throw new IllegalStateException("The workspace is already reserved during this time.");
            }
            return repo.createReservation(userId, workspaceId, date, start, end);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
