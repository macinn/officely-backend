package pw.react.backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pw.react.backend.models.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByOfficeIdAndStartDateTimeBetween(
            long officeId, LocalDateTime checkStartDate, LocalDateTime checkEndDate);

    List<Reservation> findByOfficeIdAndEndDateTimeBetween(
            long officeId, LocalDateTime checkStartDate, LocalDateTime checkEndDate);

    List<Reservation> findByOfficeIdAndStartDateTimeBeforeAndEndDateTimeAfter(
            long officeId, LocalDateTime checkStartDate, LocalDateTime checkEndDate);
    List<Reservation> findByOfficeIdOrderByStartDateTime(long officeIdeTime);
}
