package pw.react.backend.batch;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import pw.react.backend.models.Company;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

class CompanyBatchRepository implements BatchRepository<Company> {
    private final JdbcTemplate jdbcTemplate;

    CompanyBatchRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public Collection<Company> insertAll(Collection<Company> entities) {
        String sql = "INSERT INTO `COMPANY` (NAME, BOARD_MEMBERS, START_DATE) VALUES(?,?,?)";

        for (Company company : entities) {
            company.setStartDateTime(
                    Optional.ofNullable(company.getStartDateTime())
                            .orElseGet(() -> Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime())
            );
        }
        final var companies = new ArrayList<>(entities);
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Company company = companies.get(i);
                ps.setString(1, company.getName());
                ps.setInt(2, company.getBoardMembers());
                ps.setDate(3, new java.sql.Date(ZonedDateTime.of(company.getStartDateTime(), ZoneId.systemDefault()).toInstant().toEpochMilli()));
            }
            @Override
            public int getBatchSize() {
                return companies.size();
            }
        });
        return companies;
    }
}
