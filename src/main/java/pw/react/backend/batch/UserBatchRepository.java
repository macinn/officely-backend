package pw.react.backend.batch;

import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.transaction.annotation.Transactional;
import pw.react.backend.models.User;

import java.util.*;

import static pw.react.backend.models.backend.Tables.USER;

class UserBatchRepository implements BatchRepository<User> {
    private final DSLContext dsl;

    UserBatchRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    @Transactional
    public Collection<User> insertAll(Collection<User> entities) {
        List<Query> queries = new LinkedList<>();
        Long maxId = dsl.select(DSL.max(USER.ID)).from(USER).fetch().get(0).value1();
        for (User user : entities) {
            queries.add(dsl.insertInto(USER,
                            USER.ID,
                            USER.EMAIL,
                            USER.USERNAME,
                            USER.PASSWORD
                    ).values(
                            ++maxId,
                            user.getEmail(),
                            user.getUsername(),
                            user.getPassword()
                    )
            );
            user.setId(maxId);
        }
        dsl.batch(queries).execute();
        return entities;
    }
}
