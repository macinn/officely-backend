package pw.react.backend.batch;

import org.springframework.jdbc.core.JdbcTemplate;
import pw.react.backend.models.Office;

import java.util.Collection;

public class OfficeBatchRepository implements BatchRepository<Office>{
    private final JdbcTemplate jdbcTemplate;

    OfficeBatchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // TODO: implement
    @Override
    public Collection<Office> insertAll(Collection<Office> entities) {
        return null;
    }
}
