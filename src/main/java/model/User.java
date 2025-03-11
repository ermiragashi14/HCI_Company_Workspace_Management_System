package model;

import java.sql.Timestamp;

public class User {
    private int id;
    private int companyId;
    private String role;
    private String passwordHash;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String status;
    private Timestamp lastLogin;
    private Timestamp createdAt;

    public User(int id, int companyId, String role, String passwordHash, String fullName,
                String email, String phoneNumber, String status, Timestamp lastLogin, Timestamp createdAt) {
        this.id = id;
        this.companyId = companyId;
        this.role = role;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.lastLogin = lastLogin;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getLastLogin() { return lastLogin; }
    public void setLastLogin(Timestamp lastLogin) { this.lastLogin = lastLogin; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}