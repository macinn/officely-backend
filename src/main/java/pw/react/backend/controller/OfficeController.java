package pw.react.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pw.react.backend.exceptions.OfficeValidationException;
import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.models.Office;
import pw.react.backend.models.OfficePhoto;
import pw.react.backend.models.User;
import pw.react.backend.services.*;
import pw.react.backend.web.OfficeDto;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping(path = OfficeController.OFFICES_PATH)
public class OfficeController {
    private static final Logger log = LoggerFactory.getLogger(OfficeController.class);
    static final String OFFICES_PATH = "/offices";
    private final OfficeService officeService;


    private SavedService savedService;
    private UserService userService;
    private PhotoService officePhotoService;
    @Autowired
    private AzureBlobService azureBlobAdapter;

    public OfficeController(OfficeService officeService) {
        this.officeService = officeService;
    }

    @Autowired
    public void setSavedService(SavedService savedService) { this.savedService = savedService;}
    @Autowired
    public void setUserService(UserService userService) { this.userService = userService;}
    @Autowired
    public void setOfficePhotoService(PhotoService officePhotoService) {
        this.officePhotoService = officePhotoService;
    }

    @Operation(summary = "Query offices")
    @CrossOrigin
    @GetMapping(path = "")
    public ResponseEntity<Collection<OfficeDto>> getOffices(
            @RequestParam(required = true, name = "pageSize") int pageSize,
            @RequestParam(required = true, name = "pageNum") int pageNum,
            @RequestParam(required = false, name = "location") Optional<String> location,
            @Parameter(example = "2024-01-01T00:00:00.0000")
            @RequestParam(required = false, name = "availableFrom") Optional<LocalDateTime> availableFrom,
            @Parameter(example = "2025-01-01T00:00:00.0000")
            @RequestParam(required = false, name = "availableTo") Optional<LocalDateTime> availableTo,
            @RequestParam(required = false, name = "maxDistance") Optional<Integer> maxDistance,
            @RequestParam(required = false, name = "name") Optional<String> name,
            @RequestParam(required = false, name = "minPrice") Optional<Integer> minPrice,
            @RequestParam(required = false, name = "maxPrice") Optional<Integer> maxPrice,
            @RequestParam(required = false, name = "amenities") Optional<String[]> amenities,
            @RequestParam(required = false, name = "officeType") Optional<String> officeType,
            @RequestParam(required = false, name = "minRating") Optional<Integer> minRating,
            @RequestParam(required = false, name = "minArea") Optional<Integer> minArea,
            @RequestParam(required = false, name = "sort") Optional<String> sort,
            @RequestParam(required = false, name = "sortOrder") Optional<String> sortOrder,
            @RequestParam(required = false, name = "lat") Optional<Double> lat,
            @RequestParam(required = false, name = "lng") Optional<Double> lng
    ) {
        try {
            Collection<OfficeDto> newOffices = officeService.getAll(pageSize, pageNum, location, availableFrom ,
                            availableTo, maxDistance, name, minPrice, maxPrice, amenities, officeType,
                            minRating, minArea, sort, sortOrder, lat, lng)
                    .stream()
                    .map(office -> {
                        String mainPhoto = "";
                        String[] photos = new String[0];
                        Optional<OfficePhoto> mainPhotoOptional = officePhotoService.getOfficeMainPhoto(office.getId());
                        if (mainPhotoOptional.isPresent()) {
                            mainPhoto = mainPhotoOptional.get().getUrl();
                        }
                        Optional<OfficePhoto[]> photosOptional = officePhotoService.getOfficePhotos(office.getId());
                        if (photosOptional.isPresent()) {
                            photos = Arrays.stream(photosOptional.get()).map(OfficePhoto::getUrl).toArray(String[]::new);
                        }
                        return OfficeDto.valueFrom(office, mainPhoto, photos);
                    })
                    .toList();
            return ResponseEntity.status(HttpStatus.CREATED).body(newOffices);
        } catch (Exception ex) {
            throw new OfficeValidationException(ex.getMessage(), OFFICES_PATH);
        }
    }

    @Operation(summary = "Create new offices",
            description = "Admin role required")
    @CrossOrigin
    @PostMapping(path = "")
    public ResponseEntity<Collection<OfficeDto>> createOffices(@RequestBody Collection<OfficeDto> offices) {
        try {
            for(OfficeDto office : offices) {
                if(!office.mainPhoto().isBlank()) {
                    String fileName = azureBlobAdapter.uploadFromUrl(office.mainPhoto());
                    officePhotoService.storePhoto(fileName, new File(office.mainPhoto()).getName(), office.id(), true);
                }
                if(office.photos() != null) {
                    for(String fileUrl : office.photos()) {
                        String fileName = azureBlobAdapter.uploadFromUrl(fileUrl);
                        officePhotoService.storePhoto(fileName, new File(fileUrl).getName(), office.id(), true);
                    }
                }
            }
            Collection<OfficeDto> newOffices =
                    officeService.batchSave(offices.stream().map(OfficeDto::convertToOffice).toList())
                    .stream()
                            .map(office -> {
                                String mainPhoto = "";
                                String[] photos = new String[0];
                                Optional<OfficePhoto> mainPhotoOptional = officePhotoService.getOfficeMainPhoto(office.getId());
                                if (mainPhotoOptional.isPresent()) {
                                    mainPhoto = mainPhotoOptional.get().getUrl();
                                }
                                Optional<OfficePhoto[]> photosOptional = officePhotoService.getOfficePhotos(office.getId());
                                if (photosOptional.isPresent()) {
                                    photos = Arrays.stream(photosOptional.get()).map(OfficePhoto::getUrl).toArray(String[]::new);
                                }
                                return OfficeDto.valueFrom(office, mainPhoto, photos);
                            })
                    .toList();

            return ResponseEntity.status(HttpStatus.CREATED).body(newOffices);
        } catch (Exception ex) {
            throw new OfficeValidationException(ex.getMessage(), OFFICES_PATH);
        }
    }

    @Operation(summary = "Get office by id")
    @CrossOrigin
    @GetMapping(path = "/{officeId}")
    public ResponseEntity<OfficeDto> getById(@RequestHeader HttpHeaders headers, @PathVariable Long officeId) {
        OfficeDto result = officeService.getById(officeId)
                .map(office -> {
                    String mainPhoto = "";
                    String[] photos = new String[0];
                    Optional<OfficePhoto> mainPhotoOptional = officePhotoService.getOfficeMainPhoto(office.getId());
                    if (mainPhotoOptional.isPresent()) {
                        mainPhoto = mainPhotoOptional.get().getUrl();
                    }
                    Optional<OfficePhoto[]> photosOptional = officePhotoService.getOfficePhotos(office.getId());
                    if (photosOptional.isPresent()) {
                        photos = Arrays.stream(photosOptional.get()).map(OfficePhoto::getUrl).toArray(String[]::new);
                    }
                    return OfficeDto.valueFrom(office, mainPhoto, photos);
                })
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Company with %d does not exist", officeId)));
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Update office by id",
            description = "Admin role required")
    @PutMapping(path = "/{officeId}")
    @CrossOrigin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<OfficeDto> updateOffice(@RequestHeader HttpHeaders headers, @PathVariable Long officeId,
                                                  @RequestBody OfficeDto updatedOffice) {
        Office office = officeService.updateOffice(officeId, OfficeDto.convertToOffice(updatedOffice));
        String mainPhoto = "";
        String[] photos = new String[0];
        Optional<OfficePhoto> mainPhotoOptional = officePhotoService.getOfficeMainPhoto(office.getId());
        if (mainPhotoOptional.isPresent()) {
            mainPhoto = mainPhotoOptional.get().getUrl();
        }
        Optional<OfficePhoto[]> photosOptional = officePhotoService.getOfficePhotos(office.getId());
        if (photosOptional.isPresent()) {
            photos = Arrays.stream(photosOptional.get()).map(OfficePhoto::getUrl).toArray(String[]::new);
        }
        return ResponseEntity.ok(OfficeDto.valueFrom(office, mainPhoto, photos));
    }

    @Operation(summary = "Save office for later")
    @PostMapping(path = "/{officeId}/save")
    @CrossOrigin(origins = "*")
    public boolean saveForLater(Authentication authentication, @PathVariable Long officeId) {
        User user = (User)authentication.getPrincipal();
        return savedService.save(officeId, user.getId());
    }

    @Operation(summary = "Get ids of saved offices for logged user")
    @GetMapping(path = "/saved")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Collection<Long>> getSaved(Authentication authentication) {
        User user = (User)authentication.getPrincipal();
        return ResponseEntity.ok(savedService.getSavedOffices(user.getId()));
    }

    @Operation(summary = "Delete saved office")
    @DeleteMapping(path = "/{officeId}/save")
    @CrossOrigin(origins = "*")
    public boolean deleteSaved(Authentication authentication, @PathVariable Long officeId) {
        User user = (User)authentication.getPrincipal();
        return savedService.delete(officeId, user.getId());
    }

    @Operation(summary = "Delete office",
            description = "Admin role required")
    @DeleteMapping(path = "/{officeId}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> deleteOffice(@RequestHeader HttpHeaders headers, @PathVariable Long officeId) {
        boolean deleted = officeService.deleteOffice(officeId);
        if (!deleted) {
            return ResponseEntity.badRequest().body(String.format("Office with id %s does not exists.", officeId));
        }
        return ResponseEntity.ok(String.format("Office with id %s deleted.", officeId));
    }

    // Fetch available data
    @Operation(summary = "Fetch available amenities")
    @GetMapping(value = "/amenities/")
    @CrossOrigin(origins = "*")
    public @ResponseBody String[] getAmenities(@RequestHeader HttpHeaders headers) {
        return Arrays.stream(Office.Amenities.values()).map(Enum::name).toArray(String[]::new);
    }
    @Operation(summary = "Fetch available office types")
    @GetMapping(value = "/office-types/")
    @CrossOrigin(origins = "*")
    public @ResponseBody String[] getOfficeTypes(@RequestHeader HttpHeaders headers) {
        return Arrays.stream(Office.OfficeType.values()).map(Enum::name).toArray(String[]::new);
    }

}
