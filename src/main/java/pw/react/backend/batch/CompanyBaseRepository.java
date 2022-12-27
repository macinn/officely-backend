package pw.react.backend.batch;

import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.transaction.annotation.Transactional;
import pw.react.backend.models.Company;

import java.time.*;
import java.util.Collection;
import java.util.Optional;

import static pw.react.backend.models.backend.Tables.COMPANY;

class CompanyBaseRepository implements BatchRepository<Company> {
    private final DSLContext dsl;

    public CompanyBaseRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    @Transactional
    public Collection<Company> insertAll(Collection<Company> entities) {
        Query[] queries = new Query[entities.size()];
        int idx = 0;
        Long maxId = dsl.select(DSL.max(COMPANY.ID)).from(COMPANY).fetch().get(0).value1();
        for (Company company : entities) {
            LocalDateTime date = Optional.ofNullable(company.getStartDateTime())
                    .orElseGet(() -> Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime());

            queries[idx++] = dsl.insertInto(COMPANY,
                    COMPANY.ID,
                    COMPANY.BOARD_MEMBERS,
                    COMPANY.NAME,
                    COMPANY.START_DATE
            ).values(
                    maxId + idx,
                    company.getBoardMembers(),
                    company.getName(),
                    date
            );
            company.setId(maxId + idx);
            company.setStartDateTime(date);
        }
        dsl.batch(queries).execute();
        return entities;
    }
}
