package model;

import java.sql.Timestamp;

public class User {
    private int id;
    private int companyId;
    private String role;
    private String passwordHash;
    private String salt; // ✅ New field
    private String fullName;
    private String email;
    private String phoneNumber;
    private String status;
    private Timestamp createdAt;

    public User(int companyId, String role, String passwordHash, String salt, String fullName, String email, String phoneNumber, String status) {
        this.companyId = companyId;
        this.role = role;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    public User(int id, int companyId, String role, String passwordHash, String salt, String fullName, String email, String phoneNumber, String status, Timestamp createdAt) {
        this.id = id;
        this.companyId = companyId;
        this.role = role;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getCompanyId() { return companyId; }
    public String getRole() { return role; }
    public String getPasswordHash() { return passwordHash; }
    public String getSalt() { return salt; }  // ✅ Getter for salt
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getStatus() { return status; }
    public Timestamp getCreatedAt() { return createdAt; }
}
