package model;

import java.sql.Timestamp;

public class Session {
    private int id;
    private int userId;
    private Timestamp lastActive;
    private Timestamp expiresAt;
    private boolean isActive;

    public Session(int id, int userId, Timestamp lastActive, Timestamp expiresAt, boolean isActive) {
        this.id = id;
        this.userId = userId;
        this.lastActive = lastActive;
        this.expiresAt = expiresAt;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public Timestamp getLastActive() { return lastActive; }
    public void setLastActive(Timestamp lastActive) { this.lastActive = lastActive; }
    public Timestamp getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Timestamp expiresAt) { this.expiresAt = expiresAt; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }
}
