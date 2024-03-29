package pw.react.backend.services;

import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.models.Office;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface OfficeService {
    Office validateAndSave(Office office);
    Office updateOffice(Long id, Office updatedCompany) throws ResourceNotFoundException;
    boolean deleteOffice(Long companyId);
    Collection<Office> batchSave(Collection<Office> offices);
    Optional<Office> getById(long companyId);
    Collection<Office> getAll(int pageSize, int pageNum, Optional<String> location,
                              Optional<LocalDateTime> availableFrom, Optional<LocalDateTime> availableTo, Optional<Integer> maxDistance,
                              Optional<String> name, Optional<Integer> minPrice, Optional<Integer> maxPrice,
                              Optional<String[]> amenities, Optional<String> officeType, Optional<Integer> minRating,
                              Optional<Integer> minArea, Optional<String> sort, Optional<String> sortOrder,
                              Optional<Double> lat, Optional<Double> lng);

}
