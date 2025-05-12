package repository;

import dto.RecentReservationsDTO;
import utils.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
            SELECT r.date, r.start_time, r.end_time, w.name
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
                list.add(new RecentReservationsDTO(date, time, workspace));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Map<String, Integer> getWorkspaceUsageStats(int userId) {
        Map<String, Integer> usageMap = new HashMap<>();
        String query = """
            SELECT w.name, COUNT(*) AS usage_count
            FROM reservation r
            JOIN workspace w ON r.workspace_id = w.id
            WHERE r.user_id = ? AND r.status = 'CONFIRMED'
            GROUP BY w.name
            """;

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                usageMap.put(rs.getString("name"), rs.getInt("usage_count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usageMap;
    }
}
