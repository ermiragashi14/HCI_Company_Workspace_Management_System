package model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class Reservation {
    private int id;
    private int userId;
    private int workspaceId;
    private Date date;
    private Time startTime;
    private Time endTime;
    private String status;
    private String previousStatus;
    private int modifiedBy;
    private Timestamp modifiedAt;
    private Timestamp createdAt;

    public Reservation(int id, int userId, int workspaceId, Date date, Time startTime, Time endTime,
                       String status, String previousStatus, int modifiedBy, Timestamp modifiedAt, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.workspaceId = workspaceId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.previousStatus = previousStatus;
        this.modifiedBy = modifiedBy;
        this.modifiedAt = modifiedAt;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(int workspaceId) { this.workspaceId = workspaceId; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }
    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }
    public int getModifiedBy() { return modifiedBy; }
    public void setModifiedBy(int modifiedBy) { this.modifiedBy = modifiedBy; }
    public Timestamp getModifiedAt() { return modifiedAt; }
    public void setModifiedAt(Timestamp modifiedAt) { this.modifiedAt = modifiedAt; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}