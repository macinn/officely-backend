package pw.react.backend.batch;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import pw.react.backend.models.Reservation;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;

public class ReservationBatchRepository implements BatchRepository<Reservation> {
    private final JdbcTemplate jdbcTemplate;

    ReservationBatchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public Collection<Reservation> insertAll(Collection<Reservation> entities) {
        String sql = "INSERT INTO reservation (user_id, office_id, start_date_time, end_date_time) VALUES (?, ?, ?, ?)";
        final var reservations = new ArrayList<>(entities);
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Reservation reservation = reservations.get(i);
                ps.setLong(1, reservation.getUserId());
                ps.setLong(2, reservation.getOfficeId());
                ps.setDate(3, new java.sql.Date(ZonedDateTime.of(reservation.getStartDateTime(), ZoneId.systemDefault()).toInstant().toEpochMilli()));
                ps.setDate(4, new java.sql.Date(ZonedDateTime.of(reservation.getEndDateTime(), ZoneId.systemDefault()).toInstant().toEpochMilli()));
            }
            @Override
            public int getBatchSize() {
                return reservations.size();
            }
        });
        return reservations;
    }
}
