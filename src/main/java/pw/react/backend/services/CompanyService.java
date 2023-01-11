package pw.react.backend.services;

import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.models.Company;

import java.util.Collection;
import java.util.Optional;

public interface CompanyService {
    Company updateCompany(Long id, Company updatedCompany) throws ResourceNotFoundException;
    boolean deleteCompany(Long companyId);
    Collection<Company> batchSave(Collection<Company> companies);
    Optional<Company> getById(long companyId);
    Collection<Company> getAll();
}
