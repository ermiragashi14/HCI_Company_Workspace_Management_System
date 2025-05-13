package model;

import java.sql.Timestamp;

public class User {
    private int id;
    private int companyId;
    private String role;
    private String passwordHash;
    private String salt;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String status;
    private Timestamp createdAt;
    private String otpCode;
    private Timestamp otpExpiry;
    private String avatarPath;

    public User(){}

    public User(int companyId, String role, String passwordHash, String salt,
                String fullName, String email, String phoneNumber, String status) {
        this.companyId = companyId;
        this.role = role;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    public User(int id, int companyId, String role, String passwordHash, String salt, String fullName,
                String email, String phoneNumber, String status, Timestamp createdAt, String otpCode, Timestamp otpExpiry) {
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
        this.otpCode = otpCode;
        this.otpExpiry = otpExpiry;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }

    public Timestamp getOtpExpiry() { return otpExpiry; }
    public void setOtpExpiry(Timestamp otpExpiry) { this.otpExpiry = otpExpiry; }

    public String getAvatarPath() {return avatarPath;}
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }

    @Override
    public String toString() {
        return fullName;
    }

}
