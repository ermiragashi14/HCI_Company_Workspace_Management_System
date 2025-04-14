package repository;

import model.User;
import utils.DBConnector;
import java.sql.*;
import java.util.Optional;

public class UserRepository {

    public int registerUser(User user, Connection connection) throws SQLException {
        String query = "INSERT INTO user (company_id, role, password_hash, salt, full_name, email, phone_number, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

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
}
