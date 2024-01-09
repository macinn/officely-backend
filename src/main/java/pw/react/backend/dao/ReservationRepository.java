package pw.react.backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pw.react.backend.models.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
