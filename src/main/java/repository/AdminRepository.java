package repository;

import utils.DBConnector;
import service.SessionManager;

import java.sql.*;
import java.util.*;

public class AdminRepository {

    private final Connection connection;

    public AdminRepository() {
        try {
            this.connection = DBConnector.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int countTotalUsers() {
        int companyId = SessionManager.getInstance().getLoggedInCompanyId();
        String query = "SELECT COUNT(*) FROM user WHERE company_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countTotalWorkspaces() {
        int companyId = SessionManager.getInstance().getLoggedInCompanyId();
        String query = "SELECT COUNT(*) FROM workspace WHERE company_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countTotalActiveReservations() {
        int companyId = SessionManager.getInstance().getLoggedInCompanyId();
        String query = "SELECT COUNT(*) FROM reservation r JOIN user u ON r.user_id = u.id WHERE r.status = 'CONFIRMED' AND u.company_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<String, Integer> getWorkspaceUsageStats() {
        int companyId = SessionManager.getInstance().getLoggedInCompanyId();
        Map<String, Integer> stats = new HashMap<>();
        String occupiedQuery = "SELECT COUNT(*) FROM reservation r JOIN workspace w ON r.workspace_id = w.id WHERE r.status = 'CONFIRMED' AND w.company_id = ? AND r.date = CURDATE()";
        String totalQuery = "SELECT COUNT(*) FROM workspace WHERE company_id = ?";

        try (PreparedStatement occupiedStmt = connection.prepareStatement(occupiedQuery);
             PreparedStatement totalStmt = connection.prepareStatement(totalQuery)) {

            occupiedStmt.setInt(1, companyId);
            totalStmt.setInt(1, companyId);

            int occupied = 0;
            int total = 0;

            try (ResultSet rs = occupiedStmt.executeQuery()) {
                if (rs.next()) occupied = rs.getInt(1);
            }

            try (ResultSet rs = totalStmt.executeQuery()) {
                if (rs.next()) total = rs.getInt(1);
            }

            int available = Math.max(total - occupied, 0);
            stats.put("Occupied", occupied);
            stats.put("Available", available);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stats;
    }

    public Map<String, Integer> getMonthlyReservationTrends() {
        int companyId = SessionManager.getInstance().getLoggedInCompanyId();
        Map<String, Integer> trends = new LinkedHashMap<>();

        String query = "SELECT MONTHNAME(r.date) AS month, COUNT(*) AS count " +
                "FROM reservation r JOIN user u ON r.user_id = u.id " +
                "WHERE YEAR(r.date) = YEAR(CURDATE()) AND u.company_id = ? " +
                "GROUP BY MONTH(r.date), MONTHNAME(r.date) ORDER BY MONTH(r.date)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    trends.put(rs.getString("month"), rs.getInt("count"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return trends;
    }
}
