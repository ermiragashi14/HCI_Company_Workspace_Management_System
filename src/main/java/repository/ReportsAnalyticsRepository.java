package repository;

import dto.UserStatDTO;
import service.SessionManager;
import utils.DBConnector;

import java.sql.*;
import java.util.*;

public class ReportsAnalyticsRepository {

    private final Connection connection;

    public ReportsAnalyticsRepository() {
        try {
            this.connection = DBConnector.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getTotalReservations() {
        String sql = "SELECT COUNT(*) FROM reservation WHERE workspace_id IN (SELECT id FROM workspace WHERE company_id = ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, SessionManager.getInstance().getLoggedInCompanyId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getActiveReservations() {
        String sql = "SELECT COUNT(*) FROM reservation WHERE status = 'CONFIRMED' AND workspace_id IN (SELECT id FROM workspace WHERE company_id = ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, SessionManager.getInstance().getLoggedInCompanyId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<String, Integer> getReservationsPerMonth() {
        int companyId = SessionManager.getInstance().getLoggedInCompanyId();
        Map<String, Integer> trends = new LinkedHashMap<>();

        String query = "SELECT DATE_FORMAT(r.date, '%Y-%m') AS month, COUNT(*) AS count " +
                "FROM reservation r JOIN user u ON r.user_id = u.id " +
                "WHERE YEAR(r.date) = YEAR(CURDATE()) AND u.company_id = ? " +
                "GROUP BY DATE_FORMAT(r.date, '%Y-%m') " +
                "ORDER BY DATE_FORMAT(r.date, '%Y-%m')";

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


    public List<UserStatDTO> getMostActiveUsers() {
        List<UserStatDTO> users = new ArrayList<>();
        String sql = "SELECT u.full_name, COUNT(*) AS total FROM reservation r JOIN user u ON r.user_id = u.id WHERE u.company_id = ? GROUP BY u.id ORDER BY total DESC LIMIT 5";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, SessionManager.getInstance().getLoggedInCompanyId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(new UserStatDTO(rs.getString("full_name"), rs.getInt("total")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public Map<String, Integer> getReservationsByStatus() {
        Map<String, Integer> statusMap = new HashMap<>();
        String sql = "SELECT status, COUNT(*) FROM reservation r JOIN workspace w ON r.workspace_id = w.id WHERE w.company_id = ? GROUP BY status";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, SessionManager.getInstance().getLoggedInCompanyId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                statusMap.put(rs.getString("status"), rs.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statusMap;
    }
}