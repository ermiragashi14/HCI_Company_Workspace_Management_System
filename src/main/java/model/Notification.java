package model;

import java.time.LocalDateTime;

public class Notification {
    private int id;
    private int receiverId;
    private int senderId;
    private String message;
    private String type;
    private boolean readStatus;
    private LocalDateTime sentAt;
    private String senderName;
    private String receiverName;

    public Notification(int id, int receiverId, int  senderId, String message, String type, LocalDateTime sentAt, boolean readStatus) {
        this.id = id;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.message = message;
        this.type = type;
        this.sentAt=sentAt;
        this.readStatus = readStatus;
    }

    public Notification() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }
    public int getSenderId() {return senderId;}
    public void setSenderId(int senderId) {this.senderId = senderId;}
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getType() {return type;}
    public void setType(String type) {this.type = type;}
    public boolean isReadStatus() {return readStatus;}
    public void setReadStatus(boolean readStatus) {this.readStatus = readStatus;}
    public LocalDateTime getSentAt() {return sentAt;}
    public void setSentAt(LocalDateTime sentAt) {this.sentAt = sentAt;}
    public String getSenderName() {
        return senderName;
    }
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    public String getReceiverName() {
        return receiverName;
    }
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
}