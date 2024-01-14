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
    Collection<Office> getAll(int pageSize, int pageNum, String location, Optional<Integer> maxDistance, Optional<String> name, Optional<Integer> minPrice, Optional<Integer> maxPrice, Optional<String[]> amenities, Optional<String> officeType, Optional<Integer> minRating, Optional<Integer> minArea, Optional<String> sort, Optional<String> sortOrder);
}
