package pw.react.backend.batch;

import java.util.Collection;

public interface BatchRepository<T> {
    Collection<T> insertAll(Collection<T> entities);
}
