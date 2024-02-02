package pw.react.backend.services;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pw.react.backend.dao.OfficeRepository;
import pw.react.backend.exceptions.ReservationValidationException;
import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.models.Office;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class OfficeMainService implements OfficeService{
    private final Logger log = LoggerFactory.getLogger(OfficeMainService.class);

    final private OfficeRepository repository;
    private ReservationService reservationService;

    final private String geoApiKey = "AIzaSyD6qGi4RXb_I2uZtIHsoHNpNjSvPIJwGfE";

    private final GeoApiContext geoApiContext;

    protected OfficeMainService(OfficeRepository repository) {
        this.repository = repository;
        // TODO: move to config
        this.geoApiContext = new GeoApiContext.Builder()
                .apiKey(geoApiKey)
                .build();
    }

    @Autowired
    public void setReservationService(ReservationService reservationService) {
        this.reservationService = reservationService;}

    @Override
    public Office validateAndSave(Office office) {
        if (isValidOffice(office)) {
            log.info("Office is valid");
            Optional<Office> dbOffice = repository.findById(office.getId());
            if (dbOffice.isPresent()) {
                log.info("Office already exists. Updating it.");
                office.setId(dbOffice.get().getId());
            }
            office = repository.save(office);
            log.info("Office was saved.");
        }
        return office;
    }

    private boolean isValidOffice(Office office) {
        if (office != null) {
            if(office.getName().isBlank() || office.getName().isEmpty()){
                log.error("Empty office name.");
                throw new ReservationValidationException("Empty office name.");
            }
            if(office.getOfficeType() == null){
                log.error("Empty office type.");
                throw new ReservationValidationException("Empty office type.");
            }
            if(office.getOfficeArea() <= 0){
                log.error("Office area is less than 0.");
                throw new ReservationValidationException("Office area is less than 0.");
            }
            if(office.getPricePerDay() < 0){
                log.error("Office price per day is less than 0.");
                throw new ReservationValidationException("Office price per day is less than 0.");
            }
            if(office.getRating() < 0){
                log.error("Office rating is less than 0.");
                throw new ReservationValidationException("Office rating is less than 0.");
            }
            if(office.getAddress().isBlank() || office.getAddress().isEmpty()){
                log.error("Empty office address.");
                throw new ReservationValidationException("Empty office address.");
            }
            setLatLng(office);
            return true;
        }
        log.error("Office is null.");
        throw new ReservationValidationException("Office is null.");
    }

    private void setLatLng(Office office)
    {
        try {
            GeocodingResult[] results = GeocodingApi.geocode(geoApiContext, office.getAddress()).await();
            office.setLat(results[0].geometry.location.lat);
            office.setLng(results[0].geometry.location.lng);
        }
        catch (Exception e)
        {
            log.error("Error setting latitude and longitude.");
            throw new IllegalArgumentException("Error setting latitude and longitude.");
        }
    }

    @Override
    public Office updateOffice(Long id, Office updatedCompany) throws ResourceNotFoundException {
        if (repository.existsById(id)) {
            updatedCompany.setId(id);
            if(updatedCompany.getLat() == 0 || updatedCompany.getLng() == 0)
                setLatLng(updatedCompany);
            return repository.save(updatedCompany);
        }
        throw new ResourceNotFoundException(String.format("Office with id [%d] not found.", id));
    }

    @Override
    public boolean deleteOffice(Long companyId) {
        boolean result = false;
        if (repository.existsById(companyId)) {
            repository.deleteById(companyId);
            log.info("Office with id {} deleted.", companyId);
            result = true;
        }
        return result;
    }

    @Override
    public Collection<Office> batchSave(Collection<Office> offices) {
            if (offices != null && !offices.isEmpty()) {
                for (Office office : offices) {
                    if(office.getLat() == 0 || office.getLng() == 0)
                        setLatLng(office);
                }
                return repository.saveAll(offices);
            } else {
                log.warn("Offices collection is empty or null.");
                return Collections.emptyList();
            }
        }

    @Override
    public Optional<Office> getById(long companyId) {
        return repository.findById(companyId);
    }

    @Override
    public Collection<Office> getAll(int pageSize, int pageNum, Optional<String> location,
                              Optional<LocalDateTime> availableFrom, Optional<LocalDateTime> availableTo, Optional<Integer> maxDistance,
                              Optional<String> name, Optional<Integer> minPrice, Optional<Integer> maxPrice,
                              Optional<String[]> amenities, Optional<String> officeType, Optional<Integer> minRating,
                              Optional<Integer> minArea, Optional<String> sort, Optional<String> sortOrder) {
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

        try {
            if(location.isPresent()) {
                int maxDistanceValue = maxDistance.orElse(5) * 1000;
                GeocodingResult[] results = GeocodingApi.geocode(geoApiContext, location.get()).await();
                officeStream.filter(office ->
                        distance(results[0].geometry.location.lat, results[0].geometry.location.lng,
                                office.getLat(), office.getLng()) <= maxDistanceValue).toList();
            }
        }
        catch (Exception e)
        {
            log.error("Invalid location parameter.");
            throw new IllegalArgumentException("Invalid location parameter.");
        }

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
        // Filter available offices
        if(availableFrom.isPresent() && availableTo.isPresent())
            officeStream = officeStream.filter(office ->
                    reservationService.isReservationAvailable(availableFrom.get(), availableTo.get(), office.getId()));

        return officeStream.skip((long) pageNum * pageSize).limit(pageSize).toList();
    }

    // returns distance in meters
    public static double distance(double lat1, double lon1, double lat2, double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }
}
