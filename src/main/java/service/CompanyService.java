package service;

import model.Company;
import repository.CompanyRepository;
import utils.ValidationUtils;
import java.sql.Connection;
import java.sql.SQLException;

public class CompanyService {

    private final CompanyRepository companyRepository = new CompanyRepository();

    public int registerCompany(Company company, Connection conn) throws SQLException {
        validateCompany(company);
        return companyRepository.registerCompany(company, conn);
    }

    public void validateCompany(Company company) throws SQLException {
        ValidationUtils.validateCompanyFields(company);

        if (companyRepository.companyExists(company.getName(), company.getEmail())) {
            throw new IllegalArgumentException("error.company_already_exists");
        }

        String domain = ValidationUtils.getEmailDomain(company.getEmail());
        if (companyRepository.companyDomainExists(domain)) {
            throw new IllegalArgumentException("error.company_domain_taken");
        }
    }
}
