package service;

import dto.ManageReservationDTO;
import repository.ManageReservationsRepository;
import repository.WorkspaceRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManageReservationsService {
    private final ManageReservationsRepository repo = new ManageReservationsRepository();
    private final WorkspaceRepository workspaceRepo = new WorkspaceRepository();

    public List<ManageReservationDTO> filterReservations(
            int companyId,
            String userName,
            String workspaceName,
            String status,
            LocalDate date,
            String modifiedBy,
            LocalDate modifiedAt
    ) {
        try {
            return repo.getReservationsWithFilters(
                    companyId,
                    userName,
                    workspaceName,
                    status,
                    date,
                    modifiedBy,
                    modifiedAt
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ManageReservationDTO> getReservationsByUserId(int userId) {
        try {
            return repo.getReservationsByUserId(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean cancelReservation(int reservationId, int modifiedByUserId) {
        try {
            return repo.cancelReservation(reservationId, modifiedByUserId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelAllReservationsByUser(int userId, int modifierId){
        try {
            return repo.cancelAllReservationsByUser(userId, modifierId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getAllWorkspaceNames(int companyId) {
        try {
            return workspaceRepo.getWorkspaceNamesByCompanyId(companyId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
