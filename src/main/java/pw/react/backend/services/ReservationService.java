package pw.react.backend.services;

import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.models.Reservation;

import java.util.Collection;
import java.util.Optional;

public interface ReservationService {
    Reservation validateAndSave(Reservation reservation);
    Reservation updateReservation(Long id, Reservation updatedReservation) throws ResourceNotFoundException;
    boolean deleteReservation(Long reservationId);
    Collection<Reservation> batchSave(Collection<Reservation> reservations);
    Optional<Reservation> getById(long companyId);
    Collection<Reservation> getAll(int pageSize, int pageNum, Optional<Integer> userId, Optional<Integer> officeId);
}
