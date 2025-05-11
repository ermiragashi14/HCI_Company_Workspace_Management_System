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

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
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
                    users.add(user);
                }
            }
        }

        return users;
    }
}


