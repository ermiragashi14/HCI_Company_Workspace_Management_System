package repository;

import model.User;
import utils.DBConnector;
import java.sql.*;
import java.util.Optional;

public class UserRepository {


    public int registerUser(User user) throws SQLException {
        String query = "INSERT INTO user (company_id, role, password_hash, salt, full_name, email, phone_number, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, user.getCompanyId());
            stmt.setString(2, user.getRole());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getSalt());
            stmt.setString(5, user.getFullName());
            stmt.setString(6, user.getEmail());
            stmt.setString(7, user.getPhoneNumber());
            stmt.setString(8, user.getStatus());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public boolean userExists(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM user WHERE role = 'SUPER_ADMIN' AND email = ?";
        try (PreparedStatement stmt = DBConnector.getConnection().prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
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

    public boolean updateUserPassword(String email, String newPasswordHash, String newSalt) throws SQLException {
        String query = "UPDATE user SET password_hash = ?, salt = ? WHERE email = ?";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, newPasswordHash);
            stmt.setString(2, newSalt);
            stmt.setString(3, email);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean saveOtp(String email, String otpCode, Timestamp expiryTime) throws SQLException {
        String query = "UPDATE user SET otp_code = ?, otp_expiry = ? WHERE email = ?";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, otpCode);
            stmt.setTimestamp(2, expiryTime);
            stmt.setString(3, email);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public boolean removeOtp(String email) throws SQLException {
        String query = "UPDATE user SET otp_code = NULL, otp_expiry = NULL WHERE email = ?";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, email);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }



}
