package repository;

import model.Workspace;
import utils.DBConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkspaceRepository {

    public void create(Workspace workspace) {
        String sql = "INSERT INTO workspace (company_id, name, capacity, description, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, workspace.getCompanyId());
            stmt.setString(2, workspace.getName());
            stmt.setInt(3, workspace.getCapacity());
            stmt.setString(4, workspace.getDescription());
            stmt.setTimestamp(5, workspace.getCreatedAt());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Workspace> findAllByCompanyId(int companyId) {
        List<Workspace> workspaces = new ArrayList<>();
        String sql = "SELECT * FROM workspace WHERE company_id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Workspace ws = new Workspace(
                        rs.getInt("id"),
                        rs.getInt("company_id"),
                        rs.getString("name"),
                        rs.getInt("capacity"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at")
                );
                workspaces.add(ws);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workspaces;
    }

    public void deleteById(int workspaceId) {
        String sql = "DELETE FROM workspace WHERE id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, workspaceId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Workspace> searchByName(int companyId, String nameQuery) {
        List<Workspace> workspaces = new ArrayList<>();
        String sql = "SELECT * FROM workspace WHERE company_id = ? AND LOWER(name) LIKE ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            stmt.setString(2, "%" + nameQuery.toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Workspace ws = new Workspace(
                        rs.getInt("id"),
                        rs.getInt("company_id"),
                        rs.getString("name"),
                        rs.getInt("capacity"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at")
                );
                workspaces.add(ws);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workspaces;
    }
    public Workspace findById(int workspaceId) {
        String sql = "SELECT * FROM workspace WHERE id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, workspaceId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Workspace(
                        rs.getInt("id"),
                        rs.getInt("company_id"),
                        rs.getString("name"),
                        rs.getInt("capacity"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(Workspace workspace) {
        String sql = "UPDATE workspace SET name = ?, capacity = ?, description = ? WHERE id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, workspace.getName());
            stmt.setInt(2, workspace.getCapacity());
            stmt.setString(3, workspace.getDescription());
            stmt.setInt(4, workspace.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
