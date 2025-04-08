package service;

import dto.CompanyDTO;
import dto.Register.RegisterRequestDTO;
import dto.Register.RegisterResultDTO;
import model.Company;
import model.User;
import repository.UserRepository;
import utils.DBConnector;
import utils.PasswordHasher;
import utils.ValidationUtils;
import java.sql.Connection;
import java.sql.SQLException;

public class RegisterService {

    private final UserRepository userRepository = new UserRepository();
    private final CompanyService companyService = new CompanyService();

    public RegisterResultDTO registerSuperAdminAndCompany(CompanyDTO companyDTO, RegisterRequestDTO userDTO) throws Exception {
        Company company = new Company(
                0,
                companyDTO.getName(),
                companyDTO.getEmail(),
                companyDTO.getPhoneNumber(),
                null
        );

        companyService.validateCompany(company);
        validateSuperAdmin(userDTO, company.getEmail());

        try (Connection conn = DBConnector.getConnection()) {
            try {
                conn.setAutoCommit(false);

                int companyId = companyService.registerCompany(company, conn);

                User user = new User(
                        companyId,
                        "SUPER_ADMIN",
                        null,
                        null,
                        userDTO.getFullName(),
                        userDTO.getEmail(),
                        userDTO.getPhoneNumber(),
                        "ACTIVE"
                );

                int userId = createSuperAdmin(user, userDTO.getPassword(), conn);

                conn.commit();
                return new RegisterResultDTO(userId, companyId);

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private int createSuperAdmin(User user, String rawPassword, Connection conn) throws SQLException {
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

        return userRepository.registerUser(securedUser, conn);
    }

    public void validateSuperAdmin(RegisterRequestDTO dto, String companyEmail) throws SQLException {
        ValidationUtils.validateSuperAdminFields(dto, companyEmail);

        if (userRepository.userExists(dto.getEmail())) {
            throw new IllegalArgumentException("error.user_already_exists");
        }
    }

}