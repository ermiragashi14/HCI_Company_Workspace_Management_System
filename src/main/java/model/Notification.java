package model;

import java.sql.Timestamp;

public class Notification {
    private int id;
    private int userId;
    private int reservationId;
    private String message;
    private Timestamp sentAt;
    private boolean readStatus;

    public Notification(int id, int userId, int reservationId, String message, Timestamp sentAt, boolean readStatus) {
        this.id = id;
        this.userId = userId;
        this.reservationId = reservationId;
        this.message = message;
        this.sentAt = sentAt;
        this.readStatus = readStatus;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Timestamp getSentAt() { return sentAt; }
    public void setSentAt(Timestamp sentAt) { this.sentAt = sentAt; }
    public boolean isReadStatus() { return readStatus; }
    public void setReadStatus(boolean readStatus) { this.readStatus = readStatus; }
}