package service;

import repository.AdminRepository;
import java.sql.SQLException;

public class AdminService {
    private final AdminRepository adminRepository;

    public AdminService() {
        try {
            this.adminRepository = new AdminRepository();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getTotalUsers() {
        return adminRepository.countTotalUsers();
    }

    public int getTotalWorkspaces() {
        return adminRepository.countTotalWorkspaces();
    }

    public int getTotalActiveReservations() {
        return adminRepository.countTotalActiveReservations();
    }
}
