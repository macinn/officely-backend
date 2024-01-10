package pw.react.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.react.backend.dao.ReservationRepository;
import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.models.Office;
import pw.react.backend.models.Reservation;

import java.util.Collection;
import java.util.Optional;

// TODO: implement
public class ReservationMainService implements ReservationService{
    private final Logger logger = LoggerFactory.getLogger(OfficeMainService.class);
    private ReservationRepository repository;
    protected ReservationMainService(ReservationRepository repository) {
        this.repository = repository;
    }
    @Override
    public Reservation validateAndSave(Reservation reservation) {
        return null;
    }

    @Override
    public Reservation updateReservation(Long id, Reservation updatedReservation) throws ResourceNotFoundException {
        return null;
    }

    @Override
    public boolean deleteReservation(Long reservationId) {
        return false;
    }

    @Override
    public Collection<Reservation> batchSave(Collection<Reservation> reservations) {
        return null;
    }

    @Override
    public Optional<Reservation> getById(long companyId) {
        return Optional.empty();
    }

    @Override
    public Collection<Reservation> getAll() {
        return null;
    }
}
