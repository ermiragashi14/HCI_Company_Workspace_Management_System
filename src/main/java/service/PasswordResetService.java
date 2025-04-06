package service;

import model.User;
import repository.UserRepository;
import utils.PasswordHasher;
import utils.ValidationUtils;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public class PasswordResetService {
    private final UserRepository userRepository = new UserRepository();
    private static final int OTP_EXPIRY_MINUTES = 5;

    public boolean sendOtp(String email) throws SQLException {
        if (userRepository.getUserByEmail(email).isEmpty()) {
            throw new IllegalArgumentException("error.email_not_registered");
        }

        String otpCode = generateOtp();
        Timestamp expiry = Timestamp.from(Instant.now().plusSeconds(OTP_EXPIRY_MINUTES * 60));
        boolean saved = userRepository.saveOtp(email, otpCode, expiry);

        if (!saved) {
            throw new IllegalStateException("error.otp_send_failed");
        }

        EmailService.sendEmail(email, "Your OTP Code", "Use this code to reset your password: " + otpCode);
        return true;
    }

    public void resetPassword(String email, String otp, String newPassword, String confirmPassword) throws Exception {
        if (email.isEmpty() || otp.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            throw new IllegalArgumentException("error.empty_fields");
        }

        if (!ValidationUtils.doPasswordsMatch(newPassword, confirmPassword)) {
            throw new IllegalArgumentException("error.passwords_no_match");
        }

        User user = userRepository.getUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("error.email_not_registered"));

        if (user.getOtpCode() == null || user.getOtpExpiry() == null ||
                !user.getOtpCode().equals(otp) || user.getOtpExpiry().before(Timestamp.from(Instant.now()))) {
            throw new IllegalArgumentException("error.invalid_otp");
        }

        String salt = PasswordHasher.generateSalt();
        String hashedPassword = PasswordHasher.generateSaltedHash(newPassword, salt);

        boolean updated = userRepository.updateUserPassword(email, hashedPassword, salt);
        if (!updated) {
            throw new IllegalStateException("error.password_update_failed");
        }

        userRepository.removeOtp(email);
    }

    private String generateOtp() {
        int otp = 100000 + (int)(Math.random() * 900000);
        return String.valueOf(otp);
    }
}
