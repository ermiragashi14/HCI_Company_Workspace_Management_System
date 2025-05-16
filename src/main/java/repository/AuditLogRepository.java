package repository;

import dto.AuditLogDTO;
import utils.DBConnector;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AuditLogRepository {

    public void logAction(AuditLogDTO log) throws SQLException {
        String query = "INSERT INTO audit_log (user_id, company_id, action_type, details) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, log.getUserId());
            stmt.setInt(2, log.getCompanyId());
            stmt.setString(3, log.getActionType());
            stmt.setString(4, log.getDetails());
            stmt.executeUpdate();
        }
    }

    public List<AuditLogDTO> getLogsWithFilters(Integer userId, String actionType, LocalDate startDate, LocalDate endDate, int companyId) throws SQLException {
        List<AuditLogDTO> logs = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT a.*, u.full_name AS user_name, c.name AS company_name
            FROM audit_log a
            JOIN user u ON a.user_id = u.id
            JOIN company c ON a.company_id = c.id
            WHERE a.company_id = ?
        """);

        List<Object> params = new ArrayList<>();
        params.add(companyId);

        if (userId != null) {
            sql.append(" AND a.user_id = ?");
            params.add(userId);
        }

        if (actionType != null && !actionType.isBlank()) {
            sql.append(" AND a.action_type = ?");
            params.add(actionType);
        }

        if (startDate != null) {
            sql.append(" AND DATE(a.timestamp) >= ?");
            params.add(Date.valueOf(startDate));
        }

        if (endDate != null) {
            sql.append(" AND DATE(a.timestamp) <= ?");
            params.add(Date.valueOf(endDate));
        }

        sql.append(" ORDER BY a.timestamp DESC");

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            while (rs.next()) {
                logs.add(new AuditLogDTO(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("company_id"),
                        rs.getString("user_name"),
                        rs.getString("company_name"),
                        rs.getString("action_type"),
                        rs.getString("details"),
                        rs.getTimestamp("timestamp").toLocalDateTime().format(formatter)
                ));
            }
        }

        return logs;
    }

    public String getCompanyName(int companyId) {
        String query = "SELECT name FROM company WHERE id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("name");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }
}
