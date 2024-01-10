package pw.react.backend.services;

import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.models.Office;

import java.util.Collection;
import java.util.Optional;

public interface OfficeService {
    Office validateAndSave(Office office);
    Office updateOffice(Long id, Office updatedCompany) throws ResourceNotFoundException;
    boolean deleteOffice(Long companyId);
    Collection<Office> batchSave(Collection<Office> offices);
    Optional<Office> getById(long companyId);
    Collection<Office> getAll();
}
