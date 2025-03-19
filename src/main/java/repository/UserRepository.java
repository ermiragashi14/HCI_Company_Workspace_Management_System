package repository;

import model.User;
import utils.DBConnector;
import java.sql.*;

public class UserRepository {

    public int registerSuperAdmin(User user) throws SQLException {
        String query = "INSERT INTO user (company_id, role, password_hash, salt, full_name, email, phone_number, status) " +
                "VALUES (?, 'SUPER_ADMIN', ?, ?, ?, ?, ?, 'ACTIVE')";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, user.getCompanyId());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getSalt());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getPhoneNumber());

            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            } else {
                throw new SQLException("Creating Super Admin failed, no ID obtained.");
            }
        }
    }
}
