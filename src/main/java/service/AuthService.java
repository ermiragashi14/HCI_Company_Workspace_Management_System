package service;

import dto.LoginResultDTO;
import dto.LogIn.LoginRequestDTO;
import model.Company;
import model.User;
import repository.CompanyRepository;
import repository.UserRepository;
import utils.PasswordHasher;
import utils.ValidationUtils;
import java.sql.SQLException;

public class AuthService {
    private final UserRepository userRepository = new UserRepository();
    private final CompanyRepository companyRepository = new CompanyRepository();

    public LoginResultDTO loginUser(LoginRequestDTO loginDTO) throws SQLException {
        String email = loginDTO.getEmail().trim();
        String password = loginDTO.getPassword();

        if (email.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("error.empty_fields");
        }

        if (!ValidationUtils.isValidEmail(email)) {
            throw new IllegalArgumentException("error.invalid_email");
        }

        Company company = companyRepository
                .getCompanyByDomain(ValidationUtils.getEmailDomain(email))
                .orElseThrow(() -> new IllegalArgumentException("error.no_company_domain"));

        User user = userRepository
                .getUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("error.invalid_login"));

        if (user.getCompanyId() != company.getId()) {
            throw new IllegalArgumentException("error.mismatched_company");
        }

        if (!PasswordHasher.compareSaltedHash(password, user.getSalt(), user.getPasswordHash())) {
            throw new IllegalArgumentException("error.invalid_login");
        }

        return new LoginResultDTO(user.getId(), user.getCompanyId(), user.getRole());
    }
}
