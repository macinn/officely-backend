package pw.react.backend.services;

import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.models.Reservation;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReservationService {
    Reservation validateAndSave(Reservation reservation);
    Reservation updateReservation(Long id, Reservation updatedReservation) throws ResourceNotFoundException;
    boolean deleteReservation(Long reservationId);
    Collection<Reservation> batchSave(Collection<Reservation> reservations);
    Optional<Reservation> getById(long reservationId);
    Collection<Reservation> getAll(int pageSize, int pageNum, Optional<Integer> userId, Optional<Integer> officeId);
    boolean isReservationAvailable(LocalDateTime startDateTime, LocalDateTime endDateTime, long officeId);

    LocalDateTime getAvailableStartDateTime(long officeId, LocalDateTime startDateTime);
    LocalDateTime getAvailableStartEndTime(long officeId, LocalDateTime startDateTime);

}
