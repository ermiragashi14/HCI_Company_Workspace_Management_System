package repository;

import model.User;
import utils.DBConnector;
import java.sql.*;
import java.util.Optional;

public class UserRepository {

    public int registerUser(User user, Connection connection) throws SQLException {
        String query = "INSERT INTO user (company_id, role, password_hash, salt, full_name, email, phone_number, status, avatar_path) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, user.getCompanyId());
            stmt.setString(2, user.getRole());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getSalt());
            stmt.setString(5, user.getFullName());
            stmt.setString(6, user.getEmail());
            stmt.setString(7, user.getPhoneNumber());
            stmt.setString(8, user.getStatus());
            stmt.setString(9, user.getAvatarPath());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // userId i sapo krijuar
                    }
                }
            }

            throw new SQLException("Creating user failed, no ID obtained.");

        }
    }

    public boolean userExists(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM user WHERE email = ?";
        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public Optional<User> getUserByEmail(String email) throws SQLException {
        String query = "SELECT * FROM user WHERE email = ?";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new User(
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
        return Optional.empty();
    }

    public User findById(int id) {

        String query = "SELECT * FROM user WHERE id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setCompanyId(rs.getInt("company_id"));
                user.setRole(rs.getString("role"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setSalt(rs.getString("salt"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setStatus(rs.getString("status"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                user.setOtpCode(rs.getString("otp_code"));
                user.setOtpExpiry(rs.getTimestamp("otp_expiry"));
                user.setAvatarPath(rs.getString("avatar_path"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

    public void updatePhone(int userId, String phone) {
        String query = "UPDATE user SET phone_number = ? WHERE id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phone);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateAvatarPath(int userId, String path) {
        String defaultPath = "user_photos/default_avatar.png";

        String query = "UPDATE user SET avatar_path = ? WHERE id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, (path != null) ? path : defaultPath);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

