package model;

import java.sql.Timestamp;

public class Workspace {
    private int id;
    private int companyId;
    private String name;
    private int capacity;
    private String description;
    private Timestamp createdAt;

    public Workspace(int id, int companyId, String name, int capacity, String description, Timestamp createdAt) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.capacity = capacity;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}