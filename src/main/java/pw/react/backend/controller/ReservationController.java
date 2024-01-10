package pw.react.backend.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pw.react.backend.exceptions.ReservationValidationException;
import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.exceptions.UserValidationException;
import pw.react.backend.services.ReservationService;
import pw.react.backend.web.ReservationDto;
import pw.react.backend.web.ReservationDto;
import pw.react.backend.web.UserDto;

import java.util.Collection;

@RestController
@RequestMapping(path = ReservationController.RESERVATIONS_PATH)
public class ReservationController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    static final String RESERVATIONS_PATH = "/reservations";
    private final ReservationService reservationService;
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }
    @GetMapping(path = "")
    public ResponseEntity<Collection<ReservationDto>> getAll(@RequestParam("pageSize") int pageSize,
                                                        @RequestParam("pageNum") int pageNum) {
        try {
            // TODO: Paging, sorting, filtering
            Collection<ReservationDto> newReservations = reservationService.getAll()
                    .stream()
                    .map(ReservationDto::valueFrom)
                    .toList();
            log.info("Password is not going to be encoded");
            return ResponseEntity.status(HttpStatus.CREATED).body(newReservations);
        } catch (Exception ex) {
            throw new ReservationValidationException(ex.getMessage(), RESERVATIONS_PATH);
        }
    }
    @PostMapping(path = "")
    public ResponseEntity<Collection<ReservationDto>> createReservations(@RequestBody Collection<ReservationDto> Reservations) {
        try {
            Collection<ReservationDto> newReservations = reservationService.batchSave(Reservations.stream().map(ReservationDto::convertToReservation).toList())
                    .stream()
                    .map(ReservationDto::valueFrom)
                    .toList();

            return ResponseEntity.status(HttpStatus.CREATED).body(newReservations);
        } catch (Exception ex) {
            throw new ReservationValidationException(ex.getMessage(), RESERVATIONS_PATH);
        }
    }

    @GetMapping(path = "/{reservationId}")
    public ResponseEntity<ReservationDto> getById(@RequestHeader HttpHeaders headers, @PathVariable Long reservationId) {
        ReservationDto result = reservationService.getById(reservationId)
                .map(ReservationDto::valueFrom)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Company with %d does not exist", reservationId)));
        return ResponseEntity.ok(result);
    }

    @PutMapping(path = "/{reservationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateReservation(@RequestHeader HttpHeaders headers, @PathVariable Long reservationId,
                             @Valid @RequestBody ReservationDto updatedReservation) {
        reservationService.updateReservation(reservationId, ReservationDto.convertToReservation(updatedReservation));
    }

    @DeleteMapping(path = "/{reservationId}")
    public ResponseEntity<String> deleteReservation(@RequestHeader HttpHeaders headers, @PathVariable Long reservationId) {
        boolean deleted = reservationService.deleteReservation(reservationId);
        if (!deleted) {
            return ResponseEntity.badRequest().body(String.format("Reservation with id %s does not exists.", reservationId));
        }
        return ResponseEntity.ok(String.format("Reservation with id %s deleted.", reservationId));
    }
}