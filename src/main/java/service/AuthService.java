package service;

import model.Company;
import model.User;
import repository.CompanyRepository;
import repository.UserRepository;
import utils.PasswordHasher;
import utils.ValidationUtils;
import java.sql.SQLException;
import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository = new UserRepository();
    private final CompanyRepository companyRepository = new CompanyRepository();

    public User loginUser(String email, String password) throws SQLException {
        if (email.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("error.empty_fields");
        }

        if (!ValidationUtils.isValidEmail(email)) {
            throw new IllegalArgumentException("error.invalid_email");
        }

        String domain = ValidationUtils.getEmailDomain(email);
        Optional<Company> companyOpt = companyRepository.getCompanyByDomain(domain);
        if (companyOpt.isEmpty()) {
            throw new IllegalArgumentException("error.no_company_domain");
        }

        Company company = companyOpt.get();

        Optional<User> userOpt = userRepository.getUserByEmail(email);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("error.invalid_login");
        }

        User user = userOpt.get();

        if (user.getCompanyId() != company.getId()) {
            throw new IllegalArgumentException("error.mismatched_company");
        }

        if (!PasswordHasher.compareSaltedHash(password, user.getSalt(), user.getPasswordHash())) {
            throw new IllegalArgumentException("error.invalid_login");
        }

        return user;
    }
}
