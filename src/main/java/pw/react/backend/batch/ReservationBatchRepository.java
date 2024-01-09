package pw.react.backend.batch;

import org.springframework.jdbc.core.JdbcTemplate;
import pw.react.backend.models.Reservation;

import java.util.Collection;

public class ReservationBatchRepository implements BatchRepository<Reservation> {
    private final JdbcTemplate jdbcTemplate;

    ReservationBatchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public Collection<Reservation> insertAll(Collection<Reservation> entities) {
        return null;
    }
}
