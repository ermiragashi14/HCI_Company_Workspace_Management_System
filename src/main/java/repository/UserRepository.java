package repository;

import model.User;
import utils.DBConnector;
import java.sql.*;

public class UserRepository {


    public int registerSuperAdmin(User user) throws SQLException {
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

    public boolean superAdminExists(String email) throws SQLException {
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

}
