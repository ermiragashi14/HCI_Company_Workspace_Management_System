package repository;

import dto.RecentReservationsDTO;
import dto.UpcomingReservationDTO;
import utils.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class StaffRepository {

    public int countActiveReservations(int userId) {
        String query = "SELECT COUNT(*) FROM reservation WHERE user_id = ? AND status = 'CONFIRMED'";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int countTotalReservations(int userId) {
        String query = "SELECT COUNT(*) FROM reservation WHERE user_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<RecentReservationsDTO> getRecentReservations(int userId) {
        List<RecentReservationsDTO> list = new ArrayList<>();
        String query = """
            SELECT r.date, r.start_time, r.end_time, w.name, r.status
            FROM reservation r
            JOIN workspace w ON r.workspace_id = w.id
            WHERE r.user_id = ?
            ORDER BY r.date DESC, r.start_time DESC
            LIMIT 3
            """;
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String date = rs.getString("date");
                String time = rs.getString("start_time") + " - " + rs.getString("end_time");
                String workspace = rs.getString("name");
                String status = rs.getString("status");
                list.add(new RecentReservationsDTO(date, time, workspace, status));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<UpcomingReservationDTO> getUpcomingReservationsWithin24Hours(int userId) {
        List<UpcomingReservationDTO> reminders = new ArrayList<>();
        String query = """
            SELECT r.date, r.start_time, r.end_time, w.name
            FROM reservation r
            JOIN workspace w ON r.workspace_id = w.id
            WHERE r.user_id = ? AND r.status = 'CONFIRMED'
            AND TIMESTAMP(r.date, r.start_time) BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 1 DAY)
            ORDER BY r.date, r.start_time
            """;
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String date = rs.getString("date");
                String time = rs.getString("start_time") + " - " + rs.getString("end_time");
                String workspace = rs.getString("name");
                reminders.add(new UpcomingReservationDTO(date, time, workspace));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reminders;
    }

    public Map<String, Integer> getWorkspaceUsageStats(int userId) {
        Map<String, Integer> usageMap = new HashMap<>();
        String query = """
            SELECT w.name AS workspace_name, COUNT(*) AS usage_count
            FROM reservation r
            JOIN workspace w ON r.workspace_id = w.id
            WHERE r.user_id = ? AND r.status = 'CONFIRMED'
            GROUP BY w.name
            ORDER BY usage_count DESC
            """;

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("workspace_name");
                int count = rs.getInt("usage_count");
                usageMap.put(name, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usageMap;
    }
}
