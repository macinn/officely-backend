package pw.react.backend.batch;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.transaction.annotation.Transactional;
import pw.react.backend.models.Company;

import java.time.*;
import java.util.*;

import static pw.react.backend.models.backend.Tables.COMPANY;

class CompanyBatchRepository implements BatchRepository<Company> {
    private final DSLContext dsl;

    CompanyBatchRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    @Transactional
    public Collection<Company> insertAll(Collection<Company> entities) {
        List<Query> queries = new LinkedList<>();
        long maxId = Optional.ofNullable(dsl.select(DSL.max(COMPANY.ID))
                        .from(COMPANY)
                        .fetch()
                        .get(0))
                .map(Record1::value1)
                .orElseGet(() -> 0L);
        for (Company company : entities) {
            LocalDateTime date = Optional.ofNullable(company.getStartDateTime())
                    .orElseGet(() -> Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime());

            queries.add(dsl.insertInto(COMPANY,
                            COMPANY.ID,
                            COMPANY.BOARD_MEMBERS,
                            COMPANY.NAME,
                            COMPANY.START_DATE
                    ).values(
                            ++maxId,
                            company.getBoardMembers(),
                            company.getName(),
                            date
                    )
            );
            company.setId(maxId);
            company.setStartDateTime(date);
        }
        dsl.batch(queries).execute();
        return entities;
    }
}
