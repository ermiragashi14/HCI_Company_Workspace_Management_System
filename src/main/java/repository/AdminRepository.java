package repository;

import utils.DBConnector;
import model.User;
import service.SessionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminRepository {

    private final Connection connection;

    public AdminRepository() throws SQLException {
        try {
            this.connection = DBConnector.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> getAllAdmins() throws SQLException {
        int companyId = SessionManager.getInstance().getLoggedInUserId();
        String query = "SELECT u.* FROM user u WHERE u.role = 'ADMIN' AND u.company_id = ?";
        List<User> admins = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User admin = new User(
                            rs.getInt("id"),
                            rs.getInt("company_id"),
                            rs.getString("role"),
                            rs.getString("password_hash"),
                            rs.getString("salt"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("phone_number"),
                            rs.getString("status"),
                            rs.getTimestamp("created_at"),
                            rs.getString("otp_code"),
                            rs.getTimestamp("otp_expiry")
                    );
                    admins.add(admin);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching admins for company " + companyId + ": " + e.getMessage());
            throw e;
        }
        return admins;
    }

    public int countTotalUsers() {
        int companyId = SessionManager.getInstance().getLoggedInUserId();
        String query = "SELECT COUNT(*) FROM user WHERE company_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countTotalWorkspaces() {
        int companyId = SessionManager.getInstance().getLoggedInUserId();
        String query = "SELECT COUNT(*) FROM workspace w JOIN user u ON w.company_id = u.company_id WHERE u.company_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countTotalActiveReservations() {
        int companyId = SessionManager.getInstance().getLoggedInUserId();
        String query = "SELECT COUNT(*) FROM reservation r JOIN user u ON r.user_id = u.id WHERE r.status = 'active' AND u.company_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}