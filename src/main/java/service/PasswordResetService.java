package service;

import dto.PasswordReset.PasswordResetDTO;
import model.User;
import repository.PasswordResetRepository;
import repository.UserRepository;
import utils.PasswordHasher;
import utils.ValidationUtils;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;

public class PasswordResetService {
    private final UserRepository userRepository = new UserRepository();
    private final PasswordResetRepository passwordResetRepository = new PasswordResetRepository();
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int OTP_LENGTH = 6;

    public boolean sendOtp(String email) throws SQLException {
        Optional<User> userOptional = userRepository.getUserByEmail(email);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("error.email_not_registered");
        }

        String otpCode = generateOtp();
        Timestamp expiry = Timestamp.from(Instant.now().plusSeconds(OTP_EXPIRY_MINUTES * 60));
        boolean saved = passwordResetRepository.saveOtp(email, otpCode, expiry);

        if (!saved) {
            throw new IllegalStateException("error.otp_send_failed");
        }

        EmailService.sendEmail(
                email,
                "Password Reset OTP",
                "Your OTP code is: " + otpCode + ". It will expire in " + OTP_EXPIRY_MINUTES + " minutes."
        );

        return true;
    }

    public boolean verifyOtp(String email, String enteredOtp) {
        try {
            Optional<User> userOptional = userRepository.getUserByEmail(email);
            if (userOptional.isEmpty()) return false;

            User user = userOptional.get();
            return user.getOtpCode() != null &&
                    user.getOtpExpiry() != null &&
                    user.getOtpCode().equals(enteredOtp) &&
                    user.getOtpExpiry().after(Timestamp.from(Instant.now()));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void removeOtp(String email) {
        try {
            passwordResetRepository.removeOtp(email);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetPassword(PasswordResetDTO dto) throws Exception {
        String email = dto.getEmail();
        String otp = dto.getOtp();
        String newPassword = dto.getNewPassword();
        String confirmPassword = dto.getConfirmPassword();

        if (email.isEmpty() || otp.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            throw new IllegalArgumentException("error.empty_fields");
        }

        if (!ValidationUtils.doPasswordsMatch(newPassword, confirmPassword)) {
            throw new IllegalArgumentException("error.passwords_no_match");
        }

        if (!verifyOtp(email, otp)) {
            throw new IllegalArgumentException("error.invalid_otp");
        }

        String salt = PasswordHasher.generateSalt();
        String hashedPassword = PasswordHasher.generateSaltedHash(newPassword, salt);

        boolean updated = passwordResetRepository.updateUserPassword(email, hashedPassword, salt);
        if (!updated) {
            throw new IllegalStateException("error.password_update_failed");
        }

        removeOtp(email);
    }

    private String generateOtp() {
        int lowerBound = (int) Math.pow(10, OTP_LENGTH - 1);
        int upperBound = (int) Math.pow(10, OTP_LENGTH) - 1;
        return String.valueOf(new Random().nextInt(upperBound - lowerBound + 1) + lowerBound);
    }
}
