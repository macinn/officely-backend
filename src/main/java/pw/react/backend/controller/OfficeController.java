package pw.react.backend.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pw.react.backend.exceptions.OfficeValidationException;
import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.models.Office;
import pw.react.backend.services.OfficeService;
import pw.react.backend.web.OfficeDto;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping(path = OfficeController.OFFICES_PATH)
public class OfficeController {
    private static final Logger log = LoggerFactory.getLogger(OfficeController.class);
    static final String OFFICES_PATH = "/offices";
    private final OfficeService officeService;
    public OfficeController(OfficeService officeService) {
        this.officeService = officeService;
    }

    @GetMapping(path = "")
    public ResponseEntity<Collection<OfficeDto>> getAll(@RequestParam("pageSize") int pageSize,
                                                            @RequestParam("pageNum") int pageNum) {
        try {
            // TODO: Paging, sorting, filtering
            Collection<OfficeDto> newOffices = officeService.getAll()
                    .stream()
                    .map(OfficeDto::valueFrom)
                    .toList();
            log.info("Password is not going to be encoded");
            return ResponseEntity.status(HttpStatus.CREATED).body(newOffices);
        } catch (Exception ex) {
            throw new OfficeValidationException(ex.getMessage(), OFFICES_PATH);
        }
    }
    @PostMapping(path = "")
    public ResponseEntity<Collection<OfficeDto>> createOffices(@RequestBody Collection<OfficeDto> offices) {
        try {
            Collection<OfficeDto> newOffices = officeService.batchSave(offices.stream().map(OfficeDto::convertToOffice).toList())
                    .stream()
                    .map(OfficeDto::valueFrom)
                    .toList();

            return ResponseEntity.status(HttpStatus.CREATED).body(newOffices);
        } catch (Exception ex) {
            throw new OfficeValidationException(ex.getMessage(), OFFICES_PATH);
        }
    }

    @GetMapping(path = "/{officeId}")
    public ResponseEntity<OfficeDto> getById(@RequestHeader HttpHeaders headers, @PathVariable Long officeId) {
        OfficeDto result = officeService.getById(officeId)
                .map(OfficeDto::valueFrom)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Company with %d does not exist", officeId)));
        return ResponseEntity.ok(result);
    }

    @PutMapping(path = "/{officeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateOffice(@RequestHeader HttpHeaders headers, @PathVariable Long officeId,
                              @Valid @RequestBody OfficeDto updatedOffice) {
        officeService.updateOffice(officeId, OfficeDto.convertToOffice(updatedOffice));
    }

    @DeleteMapping(path = "/{officeId}")
    public ResponseEntity<String> deleteOffice(@RequestHeader HttpHeaders headers, @PathVariable Long officeId) {
        boolean deleted = officeService.deleteOffice(officeId);
        if (!deleted) {
            return ResponseEntity.badRequest().body(String.format("Office with id %s does not exists.", officeId));
        }
        return ResponseEntity.ok(String.format("Office with id %s deleted.", officeId));
    }

}
