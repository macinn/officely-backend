package pw.react.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.react.backend.dao.ReservationRepository;
import pw.react.backend.exceptions.ReservationValidationException;
import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.models.Reservation;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

public class ReservationMainService implements ReservationService{
    private final Logger log = LoggerFactory.getLogger(OfficeMainService.class);
    final private ReservationRepository repository;
    protected ReservationMainService(ReservationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Reservation validateAndSave(Reservation reservation) {
        if (isValidReservation(reservation)) {
            log.info("Reservation is valid");
            Optional<Reservation> dbReservation = repository.findById(reservation.getId());
            if (dbReservation.isPresent()) {
                log.info("Reservation already exists. Updating it.");
                reservation.setId(dbReservation.get().getId());
            }
            reservation = repository.save(reservation);
            log.info("Reservation was saved.");
        }
        return reservation;
    }

    private boolean isValidReservation(Reservation reservation) {
        if (reservation != null) {
            if(reservation.getEndDateTime().isAfter(reservation.getStartDateTime())){
                log.error("End date is before start date.");
                throw new ReservationValidationException("End date is before start date.");
            }
            if(reservation.getStartDateTime().isBefore(LocalDateTime.now()))
            {
                log.error("Start date is before current date.");
                throw new ReservationValidationException("Start date is before current date.");
            }
            return true;
        }
        log.error("Reservation is null.");
        throw new ReservationValidationException("Reservation is null.");
    }

    @Override
    public Reservation updateReservation(Long id, Reservation updatedReservation) throws ResourceNotFoundException {
        if (repository.existsById(id)) {
            updatedReservation.setId(id);
            Reservation result = repository.save(updatedReservation);
            log.info("Reservation with id {} updated.", id);
            return result;
        }
        throw new ResourceNotFoundException(String.format("Reservation with id [%d] not found.", id));
    }

    @Override
    public boolean deleteReservation(Long reservationId) {
        boolean result = false;
        if (repository.existsById(reservationId)) {
            repository.deleteById(reservationId);
            log.info("Reservation with id {} deleted.", reservationId);
            result = true;
        }
        return result;
    }

    @Override
    public Collection<Reservation> batchSave(Collection<Reservation> reservations) {
        if (reservations != null && !reservations.isEmpty()) {
            return repository.saveAll(reservations);
        } else {
            log.warn("Reservations collection is empty or null.");
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Reservation> getById(long reservationId) {
        return repository.findById(reservationId);
    }

    @Override
    public Collection<Reservation> getAll(int pageSize, int pageNum, Optional<Integer> userId, Optional<Integer> officeId) {

        Stream<Reservation> reservationStream = repository.findAll().stream();
        if(userId.isPresent()){
            reservationStream = reservationStream.filter(reservation -> reservation.getUserId() == userId.get());
        }
        if(officeId.isPresent()){
            reservationStream = reservationStream.filter(reservation -> reservation.getOfficeId() == officeId.get());
        }
        return reservationStream.skip((long) pageNum * pageSize).limit(pageSize).toList();
    }
}
