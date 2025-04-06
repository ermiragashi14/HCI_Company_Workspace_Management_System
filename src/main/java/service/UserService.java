package service;

import model.Company;
import model.User;
import repository.UserRepository;
import utils.PasswordHasher;
import utils.ValidationUtils;
import java.sql.SQLException;

public class UserService {
    private final UserRepository userRepository = new UserRepository();
    private final CompanyService companyService = new CompanyService();

    public int registerSuperAdminAndCompany(Company company, User user, String password, String confirmPassword) throws Exception {
        companyService.validateCompany(company);
        validateSuperAdmin(user, password, confirmPassword, company.getEmail());

        int companyId = companyService.registerCompany(company);

        User superAdmin = new User(
                companyId,
                "SUPER_ADMIN",
                null,
                null,
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                "ACTIVE"
        );

        return createSuperAdmin(superAdmin, password, confirmPassword, company.getEmail());
    }

    private int createSuperAdmin(User user, String rawPassword, String confirmPassword, String companyEmail) throws SQLException {

        String salt = PasswordHasher.generateSalt();
        String hashedPassword = PasswordHasher.generateSaltedHash(rawPassword, salt);

        User securedUser = new User(
                user.getCompanyId(),
                user.getRole(),
                hashedPassword,
                salt,
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getStatus()
        );

        return userRepository.registerUser(securedUser);
    }

    public void validateSuperAdmin(User user, String password, String confirmPassword, String companyEmail) throws SQLException {
        if (user.getFullName().isEmpty() || user.getEmail().isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            throw new IllegalArgumentException("error.user_empty_fields");
        }

        if (!ValidationUtils.isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("error.user_invalid_email");
        }

        if (!user.getEmail().endsWith(ValidationUtils.getEmailDomain(companyEmail))) {
            throw new IllegalArgumentException("error.user_invalid_domain");
        }

        if (!ValidationUtils.isValidPhoneNumber(user.getPhoneNumber())) {
            throw new IllegalArgumentException("error.user_invalid_phone");
        }

        if (!ValidationUtils.isValidPassword(password)) {
            throw new IllegalArgumentException("error.user_weak_password");
        }

        if (!ValidationUtils.doPasswordsMatch(password, confirmPassword)) {
            throw new IllegalArgumentException("error.user_passwords_no_match");
        }

        if (userRepository.userExists(user.getEmail())) {
            throw new IllegalArgumentException("error.user_already_exists");
        }
    }
}
