package service;

import model.User;
import repository.UserRepository;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;

public class OTPService {
    private static final int OTP_LENGTH = 6;
    private static final int EXPIRY_MINUTES = 5;
    private static final UserRepository userRepository = new UserRepository();

    public static String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public static boolean sendOtpToUser(String email) {
        try {
            String otpCode = generateOTP();
            Timestamp expiryTime = Timestamp.from(Instant.now().plusSeconds(EXPIRY_MINUTES * 60));

            boolean isSaved = userRepository.saveOtp(email, otpCode, expiryTime);
            if (!isSaved) return false;

            EmailService.sendEmail(email, "Password Reset OTP",
                    "Your OTP code is: " + otpCode + ". It will expire in " + EXPIRY_MINUTES + " minutes.");

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean verifyOTP(String email, String enteredOtp) {
        try {
            Optional<User> userOptional = userRepository.getUserByEmail(email);
            if (userOptional.isEmpty()) return false;

            User user = userOptional.get();
            if (user.getOtpCode() == null || user.getOtpExpiry() == null) return false;

            return user.getOtpCode().equals(enteredOtp) && user.getOtpExpiry().after(Timestamp.from(Instant.now()));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void removeOTP(String email) {
        try {
            userRepository.removeOtp(email);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
