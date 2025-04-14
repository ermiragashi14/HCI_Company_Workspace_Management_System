package dto;

public class CreateUserDTO {
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String confirmPassword;
    private String role;

    public CreateUserDTO(String fullName, String email, String phone, String password, String confirmPassword, String role) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.role = role;
    }

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public String getConfirmPassword() { return confirmPassword; }
    public String getRole() { return role; }
}
