package utils;

import dto.Register.RegisterRequestDTO;
import model.Company;

import java.util.regex.Pattern;

public class ValidationUtils {

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        if (email.chars().filter(c -> c == '@').count() != 1) return false;

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    public static String getEmailDomain(String email) {
        if (email == null || !email.contains("@")) return "";
        return email.substring(email.indexOf("@"));
    }

    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) return false;
        String phoneRegex = "^\\+?[0-9]{9,15}$";
        return Pattern.compile(phoneRegex).matcher(phone).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) return false;
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$";
        return Pattern.compile(passwordRegex).matcher(password).matches();
    }

    public static boolean doPasswordsMatch(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }


    public static void validateCompanyFields(Company company) {
        if (company == null) {
            throw new IllegalArgumentException("error.company_null");
        }

        if (company.getName().isEmpty() || company.getEmail().isEmpty()) {
            throw new IllegalArgumentException("error.company_empty_fields");
        }

        if (!isValidEmail(company.getEmail())) {
            throw new IllegalArgumentException("error.company_invalid_email");
        }

        if (!isValidPhoneNumber(company.getPhoneNumber())) {
            throw new IllegalArgumentException("error.company_invalid_phone");
        }
    }

    public static void validateSuperAdminFields(RegisterRequestDTO dto, String companyEmail) {
        if (dto.getFullName().isBlank() ||
                dto.getEmail().isBlank() ||
                dto.getPassword().isBlank() ||
                dto.getConfirmPassword().isBlank()) {
            throw new IllegalArgumentException("error.user_empty_fields");
        }

        if (!isValidEmail(dto.getEmail())) {
            throw new IllegalArgumentException("error.user_invalid_email");
        }

        if (!dto.getEmail().endsWith(getEmailDomain(companyEmail))) {
            throw new IllegalArgumentException("error.user_invalid_domain");
        }

        if (!isValidPhoneNumber(dto.getPhoneNumber())) {
            throw new IllegalArgumentException("error.user_invalid_phone");
        }

        if (!isValidPassword(dto.getPassword())) {
            throw new IllegalArgumentException("error.user_weak_password");
        }

        if (!doPasswordsMatch(dto.getPassword(), dto.getConfirmPassword())) {
            throw new IllegalArgumentException("error.user_passwords_no_match");
        }
    }

}
