package repository;

import model.User;
import utils.DBConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ManageUsersRepository {

    public List<User> getUsersWithFilters(int companyId, String name, String email, String status, String role, String createdAt, String currentUserRole) throws SQLException {
        List<User> users = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM user WHERE company_id = ?");

        if (name != null && !name.isEmpty()) {
            query.append(" AND full_name LIKE ?");
        }
        if (email != null && !email.isEmpty()) {
            query.append(" AND email LIKE ?");
        }
        if (status != null && !status.equalsIgnoreCase("ALL")) {
            query.append(" AND status = ?");
        }
        if (role != null && !role.equalsIgnoreCase("ALL")) {
            query.append(" AND role = ?");
        }
        if (createdAt != null && !createdAt.isEmpty()) {
            query.append(" AND DATE(created_at) = ?");
        }

        if ("ADMIN".equalsIgnoreCase(currentUserRole)) {
            query.append(" AND role = 'STAFF'");
        }

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {

            int paramIndex = 1;
            stmt.setInt(paramIndex++, companyId);

            if (name != null && !name.isEmpty()) {
                stmt.setString(paramIndex++, "%" + name + "%");
            }
            if (email != null && !email.isEmpty()) {
                stmt.setString(paramIndex++, "%" + email + "%");
            }
            if (status != null && !status.equalsIgnoreCase("ALL")) {
                stmt.setString(paramIndex++, status);
            }
            if (role != null && !role.equalsIgnoreCase("ALL")) {
                stmt.setString(paramIndex++, role);
            }
            if (createdAt != null && !createdAt.isEmpty()) {
                stmt.setDate(paramIndex++, Date.valueOf(createdAt));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getInt("company_id"),
                        rs.getString("role"),
                        rs.getString("password_hash"),
                        rs.getString("salt"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at"),
                        rs.getString("otp_code"),
                        rs.getTimestamp("otp_expiry")
                ));
            }
        }

        return users;
    }

    public User getUserById(int userId) throws SQLException {
        String query = "SELECT * FROM user WHERE id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getInt("company_id"),
                        rs.getString("role"),
                        rs.getString("password_hash"),
                        rs.getString("salt"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at"),
                        rs.getString("otp_code"),
                        rs.getTimestamp("otp_expiry")
                );
            }
        }
        return null;
    }

    public boolean updateUserStatus(int userId, String newStatus) throws SQLException {
        String query = "UPDATE user SET status = ? WHERE id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

}


