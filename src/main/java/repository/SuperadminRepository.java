package repository;

import utils.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class SuperadminRepository {

    private final Connection connection;

    public SuperadminRepository() {
        try {
            this.connection = DBConnector.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed", e);
        }
    }

    private int countByQuery(String query, int companyId) {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countAdmins(int companyId) {
        return countByQuery("SELECT COUNT(*) FROM user WHERE role = 'ADMIN' AND company_id = ? AND status = 'ACTIVE'", companyId);
    }

    public int countStaff(int companyId) {
        return countByQuery("SELECT COUNT(*) FROM user WHERE role = 'STAFF' AND company_id = ? AND status = 'ACTIVE'", companyId);
    }

    public int countWorkspaces(int companyId) {
        return countByQuery("SELECT COUNT(*) FROM workspace WHERE company_id = ?", companyId);
    }

    public int countActiveReservations(int companyId) {
        return countByQuery("SELECT COUNT(*) FROM reservation r JOIN user u ON r.user_id = u.id WHERE r.status = 'CONFIRMED' AND u.company_id = ? AND u.status = 'ACTIVE'", companyId);
    }

    public Map<String, Integer> getMonthlyReservationTrends(int companyId) {
        Map<String, Integer> trends = new LinkedHashMap<>();
        String query = "SELECT DATE_FORMAT(r.date, '%b') AS month, COUNT(*) AS count " +
                "FROM reservation r JOIN user u ON r.user_id = u.id " +
                "WHERE r.date >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) AND u.company_id = ? " +
                "GROUP BY MONTH(r.date), DATE_FORMAT(r.date, '%b') ORDER BY MONTH(r.date)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
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
