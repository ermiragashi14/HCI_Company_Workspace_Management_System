package repository;

import dto.RecentReservationsDTO;
import utils.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class StaffRepository {

    public int countActiveReservations(int userId) {
        String query = "SELECT COUNT(*) FROM reservations WHERE user_id = ? AND status = 'active'";
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
        String query = "SELECT COUNT(*) FROM reservations WHERE user_id = ?";
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
            SELECT r.reservation_date, r.start_time, r.end_time, w.workspace_name
            FROM reservations r
            JOIN workspaces w ON r.workspace_id = w.id
            WHERE r.user_id = ?
            ORDER BY r.reservation_date DESC, r.start_time DESC
            LIMIT 3
            """;
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String date = rs.getString("reservation_date");
                String time = rs.getString("start_time") + " - " + rs.getString("end_time");
                String workspace = rs.getString("workspace_name");
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
            SELECT w.workspace_name, COUNT(*) AS usage_count
            FROM reservations r
            JOIN workspaces w ON r.workspace_id = w.id
            WHERE r.user_id = ? AND r.status = 'active'
            GROUP BY w.workspace_name
            """;

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                usageMap.put(rs.getString("workspace_name"), rs.getInt("usage_count"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usageMap;
    }
}
