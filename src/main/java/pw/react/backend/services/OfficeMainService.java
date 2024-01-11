package pw.react.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.react.backend.dao.OfficeRepository;
import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.models.Office;

import java.util.Collection;
import java.util.Optional;

// TODO: implement
public class OfficeMainService implements OfficeService{
    private final Logger logger = LoggerFactory.getLogger(OfficeMainService.class);
    private OfficeRepository repository;
    protected OfficeMainService(OfficeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Office validateAndSave(Office office) {
        return null;
    }

    @Override
    public Office updateOffice(Long id, Office updatedCompany) throws ResourceNotFoundException {
        return null;
    }

    @Override
    public boolean deleteOffice(Long companyId) {
        return false;
    }

    @Override
    public Collection<Office> batchSave(Collection<Office> users) {
        return null;
    }

    @Override
    public Optional<Office> getById(long companyId) {
        return Optional.empty();
    }

    @Override
    public Collection<Office> getAll(int pageSize, int pageNum, String location, Optional<String> name, Optional<Integer> minPrice, Optional<Integer> maxPrice, Optional<String[]> amenities, Optional<String> officeType, Optional<Integer> minRating, Optional<Integer> minArea, String sort) {
        return null;
    }
}
