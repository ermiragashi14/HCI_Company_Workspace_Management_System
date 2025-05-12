package repository;

import model.Workspace;
import utils.DBConnector;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
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

    public List<String> getWorkspaceNamesByCompanyId(int companyId) throws SQLException {
        List<String> names = new ArrayList<>();
        String sql = "SELECT name FROM workspace WHERE company_id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        }
        return names;
    }
    public List<Workspace> findAvailableWorkspaces(int companyId, LocalDate date, LocalTime start, LocalTime end) {
        List<Workspace> available = new ArrayList<>();

        String sql = """
        SELECT * FROM workspace w
        WHERE w.company_id = ?
        AND NOT EXISTS (
            SELECT 1 FROM reservation r
            WHERE r.workspace_id = w.id
            AND r.date = ?
            AND (
                (r.start_time < ? AND r.end_time > ?)
                OR (r.start_time >= ? AND r.start_time < ?)
                OR (r.end_time > ? AND r.end_time <= ?)
            )
        )
    """;

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            stmt.setDate(2, Date.valueOf(date));

            stmt.setTime(3, Time.valueOf(end));
            stmt.setTime(4, Time.valueOf(start));

            stmt.setTime(5, Time.valueOf(start));
            stmt.setTime(6, Time.valueOf(end));

            stmt.setTime(7, Time.valueOf(start));
            stmt.setTime(8, Time.valueOf(end));


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
                available.add(ws);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return available;
    }



}
