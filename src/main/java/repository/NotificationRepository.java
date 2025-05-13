package repository;

import model.Notification;
import model.User;
import utils.DBConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class NotificationRepository {

    public void createNotification(int receiverId, int senderId, String message, String type) {
        String sql = "INSERT INTO notification (receiver_id, sender_id, message, type) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            stmt.setInt(2, senderId);
            stmt.setString(3, message);
            stmt.setString(4, type);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Notification> getNotificationsForUser(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE sender_id = ? OR receiver_id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Notification n = new Notification();
                n.setId(rs.getInt("id"));
                n.setReceiverId(rs.getInt("receiver_id"));
                n.setSenderId(rs.getInt("sender_id"));
                n.setMessage(rs.getString("message"));
                n.setType(rs.getString("type"));
                n.setReadStatus(rs.getBoolean("read_status"));
                n.setSentAt(rs.getTimestamp("sent_at").toLocalDateTime());
                notifications.add(n);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        enrichWithUserNames(notifications);

        return notifications;
    }

    private void enrichWithUserNames(List<Notification> notifications) {
        Set<Integer> userIds = new HashSet<>();

        for (Notification n : notifications) {
            if (n.getSenderId() != 0) userIds.add(n.getSenderId());
            if (n.getReceiverId() != 0) userIds.add(n.getReceiverId());
        }

        Map<Integer, User> userMap = getUsersById(userIds);
        for (Notification n : notifications) {
            User sender = userMap.get(n.getSenderId());
            User receiver = userMap.get(n.getReceiverId());

            n.setSenderName(sender != null ? sender.getFullName() : "(System)");
            n.setReceiverName(receiver != null ? receiver.getFullName() : "(Unknown)");
        }
    }

    private Map<Integer, User> getUsersById(Set<Integer> ids) {
        Map<Integer, User> map = new HashMap<>();
        if (ids.isEmpty()) return map;

        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = "SELECT * FROM user WHERE id IN (" + placeholders + ")";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int i = 1;
            for (Integer id : ids) {
                stmt.setInt(i++, id);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setFullName(rs.getString("full_name")); // Adjust if your column name differs
                map.put(u.getId(), u);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public boolean markAsRead(int notificationId, int userId) {
        String sql = "UPDATE notification SET read_status = TRUE WHERE id = ? AND receiver_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, notificationId);
            stmt.setInt(2, userId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
