package pw.react.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pw.react.backend.exceptions.ReservationValidationException;
import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.services.ReservationService;
import pw.react.backend.services.UserService;
import pw.react.backend.web.ReservationDto;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping(path = ReservationController.RESERVATIONS_PATH)
public class ReservationController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    static final String RESERVATIONS_PATH = "/reservations";

    private final ReservationService reservationService;
    private UserService userService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Query reservations")
    @GetMapping(path = "")
    public ResponseEntity<Collection<ReservationDto>> getAll(
            @RequestParam("pageSize") int pageSize,
            @RequestParam("pageNum") int pageNum,
            @RequestParam(required = false, name = "userId") Optional<Integer> userId,
            @RequestParam(required = false, name = "officeId") Optional<Integer> officeId
    ) {
        try {
            Collection<ReservationDto> newReservations = reservationService.getAll(pageSize, pageNum, userId, officeId)
                    .stream()
                    .map(ReservationDto::valueFrom)
                    .toList();
            log.info("Password is not going to be encoded");
            return ResponseEntity.status(HttpStatus.CREATED).body(newReservations);
        } catch (Exception ex) {
            throw new ReservationValidationException(ex.getMessage(), RESERVATIONS_PATH);
        }
    }

    @Operation(summary = "Create new reservations")
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

    @Operation(summary = "Get reservation by id")
    @GetMapping(path = "/{reservationId}")
    public ResponseEntity<ReservationDto> getById(@PathVariable Long reservationId) {
        ReservationDto result = reservationService.getById(reservationId)
                .map(ReservationDto::valueFrom)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Company with %d does not exist", reservationId)));
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Update reservation")
    @PutMapping(path = "/{reservationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateReservation(Authentication authentication, @PathVariable Long reservationId,
                             @Valid @RequestBody ReservationDto updatedReservation) {
        userService.authenticate(reservationService.getById(reservationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(String.format("Reservation with %d does not exist", reservationId)))
                .getUserId(), authentication);
        reservationService.updateReservation(reservationId, ReservationDto.convertToReservation(updatedReservation));
    }

    @Operation(summary = "Delete reservation")
    @DeleteMapping(path = "/{reservationId}")
    public ResponseEntity<String> deleteReservation(Authentication authentication, @PathVariable Long reservationId) {

        userService.authenticate(reservationService.getById(reservationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(String.format("Reservation with %d does not exist", reservationId)))
                .getUserId(), authentication);
        boolean deleted = reservationService.deleteReservation(reservationId);
        if (!deleted) {
            return ResponseEntity.badRequest().body(String.format("Reservation with id %s does not exists.", reservationId));
        }
        return ResponseEntity.ok(String.format("Reservation with id %s deleted.", reservationId));
    }
}
