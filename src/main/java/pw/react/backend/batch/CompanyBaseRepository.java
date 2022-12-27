package pw.react.backend.batch;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.transaction.annotation.Transactional;
import pw.react.backend.models.Company;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;

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
        Record1<Long> maxId = dsl.select(DSL.max(COMPANY.ID)).from(COMPANY).fetch().get(0);
        for (Company company : entities) {
            queries[idx++] = dsl.insertInto(COMPANY,
                    COMPANY.ID,
                    COMPANY.BOARD_MEMBERS,
                    COMPANY.NAME,
                    COMPANY.START_DATE
            ).values(
                    maxId.value1() + idx,
                    company.getBoardMembers(),
                    company.getName(),
                    ZonedDateTime.of(company.getStartDateTime(), ZoneId.of("UTC")).toLocalDateTime()
            );
            company.setId(maxId.value1() + idx);
        }
        dsl.batch(queries).execute();
        return entities;
    }
}
