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
        String query = "SELECT COUNT(*) FROM user WHERE company_id = ? AND status = 'ACTIVE'";
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

    public int countTotalReservations() {
        int companyId = SessionManager.getInstance().getLoggedInCompanyId();
        String query = """
        SELECT COUNT(*) FROM reservation r
        JOIN user u ON r.user_id = u.id
        WHERE u.company_id = ? AND r.status != 'CANCELED'
    """;
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

    public Map<String, Integer> getMonthlyReservationTrends() {
        int companyId = SessionManager.getInstance().getLoggedInCompanyId();
        int adminId = SessionManager.getInstance().getLoggedInUserId();
        Map<String, Integer> trends = new LinkedHashMap<>();

        String query = "SELECT MONTHNAME(r.date) AS month, COUNT(*) AS count " +
                "FROM reservation r JOIN user u ON r.user_id = u.id " +
                "WHERE YEAR(r.date) = YEAR(CURDATE()) AND u.company_id = ? " +
                "AND (u.role = 'STAFF' OR u.id = ?) " +
                "GROUP BY MONTH(r.date), MONTHNAME(r.date) ORDER BY MONTH(r.date)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, companyId);
            stmt.setInt(2, adminId);
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
    public Map<String, Integer> getMostUsedWorkspaces() {
        int companyId = SessionManager.getInstance().getLoggedInCompanyId();
        Map<String, Integer> usage = new LinkedHashMap<>();

        String query = """
        SELECT w.name, COUNT(*) AS usage_count
        FROM reservation r
        JOIN workspace w ON r.workspace_id = w.id
        JOIN user u ON r.user_id = u.id
        WHERE u.company_id = ? AND r.status = 'CONFIRMED'
        GROUP BY w.name
        ORDER BY usage_count DESC
        LIMIT 5
    """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                usage.put(rs.getString("name"), rs.getInt("usage_count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usage;
    }

}
