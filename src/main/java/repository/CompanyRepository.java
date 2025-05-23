package repository;

import model.Company;
import utils.DBConnector;
import java.sql.*;
import java.util.Optional;

import static utils.DBConnector.getConnection;

public class CompanyRepository {

    public int registerCompany(Company company, Connection connection) throws SQLException {
        String query = "INSERT INTO company (name, email, phone_number) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, company.getName());
            stmt.setString(2, company.getEmail());
            stmt.setString(3, company.getPhoneNumber());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int companyId = generatedKeys.getInt(1);
                        return companyId;
                    }
                }
            }

            return -1;
        }
    }

    public boolean companyExists(String name, String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM company WHERE name = ? OR email = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public boolean companyDomainExists(String domain) throws SQLException {
        String query = "SELECT COUNT(*) FROM company WHERE email LIKE ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, "%" + domain);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public Optional<Company> getCompanyByDomain(String domain) throws SQLException {
        String query = "SELECT * FROM company WHERE email LIKE ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + domain);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new Company(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        rs.getTimestamp("created_at")
                ));
            }
        }
        return Optional.empty();
    }

    public String getCompanyEmail(int companyId) throws SQLException {
        String query = "SELECT email FROM company WHERE id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("email");
            } else {
                throw new IllegalArgumentException("Company not found for ID: " + companyId);
            }
        }
    }

    public Company findById(int id) {
        String query = "SELECT * FROM company WHERE id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Company company = new Company();
                company.setId(rs.getInt("id"));
                company.setName(rs.getString("name"));
                company.setEmail(rs.getString("email"));
                company.setPhoneNumber(rs.getString("phone_number"));
                company.setCreatedAt(rs.getTimestamp("created_at"));
                return company;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
