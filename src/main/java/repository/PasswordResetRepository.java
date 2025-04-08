package repository;

import utils.DBConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PasswordResetRepository {

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

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean removeOtp(String email) throws SQLException {
        String query = "UPDATE user SET otp_code = NULL, otp_expiry = NULL WHERE email = ?";

        try (Connection connection = DBConnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, email);
            return stmt.executeUpdate() > 0;
        }
    }
}
