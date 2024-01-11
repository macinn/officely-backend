package pw.react.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import pw.react.backend.dao.OfficeRepository;
import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.models.Office;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

// TODO: implement
public class OfficeMainService implements OfficeService{
    private final Logger log = LoggerFactory.getLogger(OfficeMainService.class);
    final private OfficeRepository repository;
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
    public Collection<Office> getAll(int pageSize, int pageNum, String location, Optional<String> name, Optional<Integer> minPrice, Optional<Integer> maxPrice, Optional<String[]> amenities, Optional<String> officeType, Optional<Integer> minRating, Optional<Integer> minArea, Optional<String> sort, Optional<String> sortOrder) {
        Stream<Office> officeStream = null;
        try{
            if(sort.isPresent())
            {
                Sort sorting = Sort.by(sort.get()).ascending();
                if(sortOrder.isPresent() && ( sortOrder.get().equalsIgnoreCase("desc") || sortOrder.get().equalsIgnoreCase("descending")))
                {
                    sorting = sorting.descending();
                }
                officeStream = repository.findAll(sorting).stream();
            }
        }
        catch (IllegalArgumentException e)
        {
            log.error("Invalid sort parameter.");
            throw new IllegalArgumentException("Invalid sort parameter.");
        }
        if(officeStream == null)
            officeStream = repository.findAll().stream();

        if(name.isPresent()){
            officeStream = officeStream.filter(office -> office.getName().equals(name.get()));
        }
        if(minPrice.isPresent()){
            officeStream = officeStream.filter(office -> office.getPricePerDay() >= minPrice.get());
        }
        if(maxPrice.isPresent())
        {
            officeStream = officeStream.filter(office -> office.getPricePerDay() <= maxPrice.get());
        }
        if(amenities.isPresent())
        {
            long amenitiesMask = Office.Amenities.getAmenitiesMask(amenities.get());
            officeStream = officeStream.filter(office -> (office.getAmenities() & amenitiesMask) == amenitiesMask);
        }
        if(officeType.isPresent())
        {
            officeStream = officeStream.filter(office -> office.getOfficeType().name().equalsIgnoreCase(officeType.get()));
        }
        if(minRating.isPresent())
        {
            officeStream = officeStream.filter(office -> office.getRating() >= minRating.get());
        }
        if(minArea.isPresent())
        {
            officeStream = officeStream.filter(office -> office.getOfficeArea() >= minArea.get());
        }

        return officeStream.skip(Math.max(0, (pageNum - 1) * pageSize)).limit(pageSize).toList();
    }
}
