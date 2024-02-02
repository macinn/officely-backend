package pw.react.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pw.react.backend.exceptions.OfficeValidationException;
import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.models.Office;
import pw.react.backend.models.OfficePhoto;
import pw.react.backend.models.User;
import pw.react.backend.services.OfficeService;
import pw.react.backend.services.PhotoService;
import pw.react.backend.services.SavedService;
import pw.react.backend.services.UserService;
import pw.react.backend.web.OfficeDto;
import pw.react.backend.web.UploadFileResponse;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

// TODO: Completely change how images are handled
@RestController
@RequestMapping(path = OfficeController.OFFICES_PATH)
public class OfficeController {
    private static final Logger log = LoggerFactory.getLogger(OfficeController.class);
    static final String OFFICES_PATH = "/offices";
    private final OfficeService officeService;

    private PhotoService officePhotoService;
    private SavedService savedService;
    private UserService userService;

    public OfficeController(OfficeService officeService) {
        this.officeService = officeService;
    }

    @Autowired
    public void setOfficePhotoService(PhotoService officePhotoService) {
        this.officePhotoService = officePhotoService;
    }
    @Autowired
    public void setSavedService(SavedService savedService) { this.savedService = savedService;}
    @Autowired
    public void setUserService(UserService userService) { this.userService = userService;}

    @Operation(summary = "Query offices")
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
            @RequestParam(required = false, name = "sortOrder") Optional<String> sortOrder
    ) {
        try {
            Collection<OfficeDto> newOffices = officeService.getAll(pageSize, pageNum, location, availableFrom ,
                            availableTo, maxDistance, name, minPrice, maxPrice, amenities, officeType,
                            minRating, minArea, sort, sortOrder)
                    .stream()
                    .map(OfficeDto::valueFrom)
                    .toList();
            return ResponseEntity.status(HttpStatus.CREATED).body(newOffices);
        } catch (Exception ex) {
            throw new OfficeValidationException(ex.getMessage(), OFFICES_PATH);
        }
    }

    @Operation(summary = "Create new offices",
            description = "Admin role required")
    @PostMapping(path = "")
    public ResponseEntity<Collection<OfficeDto>> createOffices(@RequestBody Collection<OfficeDto> offices) {
        try {
            Collection<OfficeDto> newOffices =
                    officeService.batchSave(offices.stream().map(OfficeDto::convertToOffice).toList())
                    .stream()
                    .map(OfficeDto::valueFrom)
                    .toList();

            return ResponseEntity.status(HttpStatus.CREATED).body(newOffices);
        } catch (Exception ex) {
            throw new OfficeValidationException(ex.getMessage(), OFFICES_PATH);
        }
    }

    @Operation(summary = "Get office by id")
    @GetMapping(path = "/{officeId}")
    public ResponseEntity<OfficeDto> getById(@RequestHeader HttpHeaders headers, @PathVariable Long officeId) {
        OfficeDto result = officeService.getById(officeId)
                .map(OfficeDto::valueFrom)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Company with %d does not exist", officeId)));
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Update office by id",
            description = "Admin role required")
    @PutMapping(path = "/{officeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOffice(@RequestHeader HttpHeaders headers, @PathVariable Long officeId,
                              @Valid @RequestBody OfficeDto updatedOffice) {
        officeService.updateOffice(officeId, OfficeDto.convertToOffice(updatedOffice));
    }

    @Operation(summary = "Save office for later")
    @PostMapping(path = "/{officeId}/save")
    public boolean saveForLater(Authentication authentication, @PathVariable Long officeId) {
        User user = (User)authentication.getPrincipal();
        return savedService.save(officeId, user.getId());
    }

    @Operation(summary = "Delete saved office")
    @DeleteMapping(path = "/{officeId}/save")
    public boolean deleteSaved(Authentication authentication, @PathVariable Long officeId) {
        User user = (User)authentication.getPrincipal();
        return savedService.delete(officeId, user.getId());
    }

    @Operation(summary = "Delete office",
            description = "Admin role required")
    @DeleteMapping(path = "/{officeId}")
    public ResponseEntity<String> deleteOffice(@RequestHeader HttpHeaders headers, @PathVariable Long officeId) {
        boolean deleted = officeService.deleteOffice(officeId);
        if (!deleted) {
            return ResponseEntity.badRequest().body(String.format("Office with id %s does not exists.", officeId));
        }
        return ResponseEntity.ok(String.format("Office with id %s deleted.", officeId));
    }

    // Photos
    @Operation(summary = "Get main photo for company")
    @GetMapping(value = "/{officeId}/thumbnail", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] getThumbnail(@RequestHeader HttpHeaders headers, @PathVariable Long officeId) {

        Optional<Office> office = officeService.getById(officeId);
        if(office.isPresent()) {
            String thumbnailId = office.get().getOfficePhotoThumbnailId();
            Optional<OfficePhoto> officeThumbnail = officePhotoService.getPhoto(thumbnailId);
            return officeThumbnail.map(OfficePhoto::getData).orElseGet(() -> new byte[0]);
        }
        else
            throw new ResourceNotFoundException(String.format("Office with %d does not exist", officeId));
    }

    @Operation(summary = "Upload main photo for company")
    @PostMapping("/{officeId}/thumbnail")
    public ResponseEntity<UploadFileResponse> uploadThumbnail(@RequestHeader HttpHeaders headers,
                                                          @PathVariable Long officeId,
                                                          @RequestParam(value="file") MultipartFile file) {

        OfficePhoto officePhoto = officePhotoService.storePhoto(officeId, file);
        Optional<Office> office = officeService.getById(officeId);

        if(office.isPresent()) {
            office.get().setOfficePhotoThumbnailId(officePhoto.getId());
            officeService.updateOffice(officeId, office.get());
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/companies/" + officeId + "/logo/")
                    .path(officePhoto.getFileName())
                    .toUriString();
            UploadFileResponse response = new UploadFileResponse(
                    officePhoto.getFileName(),
                    fileDownloadUri,
                    file.getContentType(),
                    file.getSize()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @Operation(summary = "Get all photos of a company")
    @GetMapping(value = "/{officeId}/photos", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[][] getPhotos(@RequestHeader HttpHeaders headers, @PathVariable Long officeId) {
        Optional<OfficePhoto[]> officePhotos = officePhotoService.getOfficePhotos(officeId);
        return officePhotos.map(photos -> Arrays.stream(photos)
                .map(OfficePhoto::getData).toArray(byte[][]::new)).orElseGet(() -> new byte[0][]);

    }

    @Operation(summary = "Upload photo for company")
    @PostMapping("/{officeId}/photos")
    public ResponseEntity<UploadFileResponse> uploadPhoto(@RequestHeader HttpHeaders headers,
                                                         @PathVariable Long officeId,
                                                         @RequestParam("file") MultipartFile file) {
        OfficePhoto officePhoto = officePhotoService.storePhoto(officeId, file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/companies/" + officeId + "/logo/")
                .path(officePhoto.getFileName())
                .toUriString();
        UploadFileResponse response = new UploadFileResponse(
                officePhoto.getFileName(),
                fileDownloadUri,
                file.getContentType(),
                file.getSize()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Delete photos for given company",
            description = "Admin role required")
    @DeleteMapping(value = "/{officeId}/photos/{photoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePhotos    (@RequestHeader HttpHeaders headers, @PathVariable Long officeId, @PathVariable String photoId) {
        officePhotoService.deleteById(photoId);
    }

    // Fetch available data
    @Operation(summary = "Fetch available amenities")
    @GetMapping(value = "/amenities/")
    public @ResponseBody String[] getAmenities(@RequestHeader HttpHeaders headers) {
        return Arrays.stream(Office.Amenities.values()).map(Enum::name).toArray(String[]::new);
    }
    @Operation(summary = "Fetch available office types")
    @GetMapping(value = "/office-types/")
    public @ResponseBody String[] getOfficeTypes(@RequestHeader HttpHeaders headers) {
        return Arrays.stream(Office.OfficeType.values()).map(Enum::name).toArray(String[]::new);
    }

}
