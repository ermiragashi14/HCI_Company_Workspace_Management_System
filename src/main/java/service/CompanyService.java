package service;

import model.Company;
import repository.CompanyRepository;
import utils.ValidationUtils;
import java.sql.SQLException;

public class CompanyService {

    private final CompanyRepository companyRepo = new CompanyRepository();

    public int registerCompany(Company company) throws SQLException {
        validateCompany(company);
        return companyRepo.registerCompany(company);
    }

    public void validateCompany(Company company) throws SQLException {
        if (company.getName().isEmpty() || company.getEmail().isEmpty()) {
            throw new IllegalArgumentException("error.company_empty_fields");
        }

        if (!ValidationUtils.isValidEmail(company.getEmail())) {
            throw new IllegalArgumentException("error.company_invalid_email");
        }

        if (!ValidationUtils.isValidPhoneNumber(company.getPhoneNumber())) {
            throw new IllegalArgumentException("error.company_invalid_phone");
        }

        if (companyRepo.companyExists(company.getName(), company.getEmail())) {
            throw new IllegalArgumentException("error.company_already_exists");
        }

        String domain = ValidationUtils.getEmailDomain(company.getEmail());
        if (companyRepo.companyDomainExists(domain)) {
            throw new IllegalArgumentException("error.company_domain_taken");
        }
    }
}
