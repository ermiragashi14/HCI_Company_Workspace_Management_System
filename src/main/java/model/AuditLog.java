package model;

import java.sql.Timestamp;

public class AuditLog {
    private int id;
    private int userId;
    private int companyId;
    private String actionType;
    private String details;
    private Timestamp timestamp;

    public AuditLog(int id, int userId, int companyId, String actionType, String details, Timestamp timestamp) {
        this.id = id;
        this.userId = userId;
        this.companyId = companyId;
        this.actionType = actionType;
        this.details = details;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
