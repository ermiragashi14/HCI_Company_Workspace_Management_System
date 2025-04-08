package dto.PasswordReset;

public class PasswordResetDTO {
    private String email;
    private String otp;
    private String newPassword;
    private String confirmPassword;

    public PasswordResetDTO(String email, String otp, String newPassword, String confirmPassword) {
        this.email = email;
        this.otp = otp;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() { return email; }
    public String getOtp() { return otp; }
    public String getNewPassword() { return newPassword; }
    public String getConfirmPassword() { return confirmPassword; }
}
