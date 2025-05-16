package repository;

import dto.NewReservationDTO;
import utils.DBConnector;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class NewReservationRepository {

    public List<NewReservationDTO> getAvailableWorkspaces(LocalDate date, LocalTime start, LocalTime end, int companyId) {
        List<NewReservationDTO> result = new ArrayList<>();
        String sql = "SELECT w.id, w.name FROM workspace w " +
                "WHERE w.company_id = ? AND w.id NOT IN (" +
                "SELECT r.workspace_id FROM reservation r WHERE r.date = ? " +
                "AND r.status = 'CONFIRMED' AND r.start_time < ? AND r.end_time > ?" +
                ")";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setTime(3, Time.valueOf(end));
            stmt.setTime(4, Time.valueOf(start));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(new NewReservationDTO(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean createReservation(int userId, int workspaceId, LocalDate date, LocalTime start, LocalTime end) {
        String sql = "INSERT INTO reservation (user_id, workspace_id, date, start_time, end_time, status, created_at) " +
                "VALUES (?, ?, ?, ?, ?, 'CONFIRMED', NOW())";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, workspaceId);
            stmt.setDate(3, Date.valueOf(date));
            stmt.setTime(4, Time.valueOf(start));
            stmt.setTime(5, Time.valueOf(end));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isWorkspaceAvailable(int workspaceId, LocalDate date, LocalTime start, LocalTime end) {
        String sql = "SELECT COUNT(*) FROM reservation " +
                "WHERE workspace_id = ? AND date = ? AND status = 'CONFIRMED' " +
                "AND start_time < ? AND end_time > ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, workspaceId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setTime(3, Time.valueOf(end));
            stmt.setTime(4, Time.valueOf(start));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


}
