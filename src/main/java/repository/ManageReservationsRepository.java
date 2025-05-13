package repository;

import dto.ManageReservationDTO;
import utils.DBConnector;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ManageReservationsRepository {

    public List<ManageReservationDTO> getReservationsWithFilters(
            int companyId,
            String userName,
            String workspaceName,
            String status,
            java.time.LocalDate date,
            String modifiedBy,
            java.time.LocalDate modifiedDate
    ) throws SQLException {
        List<ManageReservationDTO> dtos = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT r.*, u.full_name AS user_full_name, w.name AS workspace_name, m.full_name AS modified_by_name " +
                        "FROM reservation r " +
                        "JOIN user u ON r.user_id = u.id " +
                        "JOIN workspace w ON r.workspace_id = w.id " +
                        "LEFT JOIN user m ON r.modified_by = m.id " +
                        "WHERE u.company_id = ?"
        );

        List<Object> params = new ArrayList<>();
        params.add(companyId);

        if (userName != null && !userName.isBlank()) {
            sql.append(" AND u.full_name LIKE ?");
            params.add("%" + userName.trim() + "%");
        }
        if (workspaceName != null && !workspaceName.isBlank()) {
            sql.append(" AND w.name LIKE ?");
            params.add("%" + workspaceName.trim() + "%");
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND r.status = ?");
            params.add(status);
        }
        if (date != null) {
            sql.append(" AND r.date = ?");
            params.add(Date.valueOf(date));
        }
        if (modifiedBy != null && !modifiedBy.isBlank()) {
            sql.append(" AND m.full_name LIKE ?");
            params.add("%" + modifiedBy.trim() + "%");
        }
        if (modifiedDate != null) {
            sql.append(" AND DATE(r.modified_at) = ?");
            params.add(Date.valueOf(modifiedDate));
        }

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
            DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            while (rs.next()) {
                String schedule = rs.getTime("start_time").toLocalTime().format(timeFormat) + " - " +
                        rs.getTime("end_time").toLocalTime().format(timeFormat);

                ManageReservationDTO dto = new ManageReservationDTO(
                        rs.getInt("id"),
                        rs.getString("user_full_name"),
                        rs.getString("workspace_name"),
                        rs.getDate("date").toLocalDate().format(dateFormat),
                        schedule,
                        rs.getString("status"),
                        rs.getString("previous_status"),
                        rs.getString("modified_by_name"),
                        rs.getTimestamp("modified_at") != null ? rs.getTimestamp("modified_at").toLocalDateTime().format(dateTimeFormat) : "",
                        rs.getTimestamp("created_at").toLocalDateTime().format(dateTimeFormat)
                );
                dtos.add(dto);
            }
        }

        return dtos;
    }

    public List<ManageReservationDTO> getReservationsByUserId(int userId) throws SQLException {
        List<ManageReservationDTO> dtos = new ArrayList<>();
        String sql = "SELECT r.*, w.name AS workspace_name " +
                "FROM reservation r " +
                "JOIN workspace w ON r.workspace_id = w.id " +
                "WHERE r.user_id = ? ORDER BY r.date DESC, r.start_time";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
            DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            while (rs.next()) {
                String schedule = rs.getTime("start_time").toLocalTime().format(timeFormat) + " - " +
                        rs.getTime("end_time").toLocalTime().format(timeFormat);

                ManageReservationDTO dto = new ManageReservationDTO(
                        rs.getInt("id"),
                        "",
                        rs.getString("workspace_name"),
                        rs.getDate("date").toLocalDate().format(dateFormat),
                        schedule,
                        rs.getString("status"),
                        rs.getString("previous_status"),
                        "",
                        "",
                        rs.getTimestamp("created_at").toLocalDateTime().format(dateTimeFormat)
                );
                dtos.add(dto);
            }
        }

        return dtos;
    }

    public boolean cancelReservation(int reservationId, int modifierId) throws SQLException {
        String query = "UPDATE reservation SET status = 'CANCELED', previous_status = status, modified_by = ?, modified_at = NOW() WHERE id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, modifierId);
            stmt.setInt(2, reservationId);
            return stmt.executeUpdate() > 0;
        }
    }
}
