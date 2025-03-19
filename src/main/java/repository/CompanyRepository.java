package repository;

import model.Company;
import utils.DBConnector;

import java.sql.*;

public class CompanyRepository {

    public int registerCompany(Company company) throws SQLException {
        String query = "INSERT INTO company (name, email, phone_number) VALUES (?, ?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, company.getName());
            stmt.setString(2, company.getEmail());
            stmt.setString(3, company.getPhoneNumber());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Company registration failed.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating company failed, no ID obtained.");
                }
            }
        }
    }
}
