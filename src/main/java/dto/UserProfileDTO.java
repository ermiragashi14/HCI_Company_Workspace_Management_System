package dto;

public class UserProfileDTO {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarPath;
    private String companyName;
    private String companyEmail;

    public UserProfileDTO(String fullName, String email, String phoneNumber, String avatarPath, String companyName, String companyEmail) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.avatarPath = avatarPath;
        this.companyName = companyName;
        this.companyEmail = companyEmail;
    }

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAvatarPath() { return avatarPath; }
    public String getCompanyName() { return companyName; }
    public String getCompanyEmail() { return companyEmail; }
}
