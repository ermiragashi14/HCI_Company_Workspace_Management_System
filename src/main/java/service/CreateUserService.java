package service;

import dto.CreateUserDTO;
import model.User;
import repository.CompanyRepository;
import repository.UserRepository;
import utils.DBConnector;
import utils.PasswordHasher;
import utils.ValidationUtils;

import java.io.File;
import java.sql.Connection;

public class CreateUserService {

    private final UserRepository userRepo = new UserRepository();
    private final CompanyRepository companyRepo = new CompanyRepository();

    public int createUser(String creatorRole, int creatorCompanyId, CreateUserDTO dto) throws Exception {
        validateInputs(dto);
        validateRoleAccess(creatorRole, dto.getRole());

        String companyEmail = companyRepo.getCompanyEmail(creatorCompanyId);
        String companyDomain = ValidationUtils.getEmailDomain(companyEmail);
        String newUserDomain = ValidationUtils.getEmailDomain(dto.getEmail());

        if (!newUserDomain.equalsIgnoreCase(companyDomain)) {
            throw new IllegalArgumentException("error.user_invalid_domain");
        }

        if (userRepo.userExists(dto.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        String salt = PasswordHasher.generateSalt();
        String hashed = PasswordHasher.generateSaltedHash(dto.getPassword(), salt);

        User user = new User(
                creatorCompanyId,
                dto.getRole(),
                hashed,
                salt,
                dto.getFullName(),
                dto.getEmail(),
                dto.getPhone(),
                "ACTIVE"
        );

        //avatarpath
        String defaultAvatarPath = "user_photos/default_avatar.png";
        File defaultAvatar = new File(defaultAvatarPath);
        if (!defaultAvatar.exists()) {
            throw new IllegalStateException("Default avatar not found at: " + defaultAvatarPath);
        }
        user.setAvatarPath(defaultAvatarPath);

        try (Connection conn = DBConnector.getConnection()) {
            return userRepo.registerUser(user, conn);
        }
    }

    private void validateInputs(CreateUserDTO dto) {
        if (!ValidationUtils.isValidEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        if (!ValidationUtils.isValidPhoneNumber(dto.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number.");
        }

        if (!ValidationUtils.isValidPassword(dto.getPassword())) {
            throw new IllegalArgumentException("Password is not strong enough.");
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        if (dto.getFullName() == null || dto.getFullName().isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }

        if (dto.getRole() == null || dto.getRole().isBlank()) {
            throw new IllegalArgumentException("Role is required.");
        }
    }

    private void validateRoleAccess(String creatorRole, String newUserRole) {
        if ("SUPER_ADMIN".equals(creatorRole)) {
            if (!newUserRole.equals("ADMIN") && !newUserRole.equals("STAFF")) {
                throw new IllegalArgumentException("SUPER_ADMIN can only create ADMIN or STAFF users.");
            }
        } else if ("ADMIN".equals(creatorRole)) {
            if (!newUserRole.equals("STAFF")) {
                throw new IllegalArgumentException("ADMIN can only create STAFF users.");
            }
        } else {
            throw new IllegalArgumentException("You are not authorized to create users.");
        }
    }
}
