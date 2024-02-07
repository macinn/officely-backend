package pw.react.backend.services;

import java.util.Collection;

public interface SavedService {
    boolean delete(Long officeId, Long userId);
    boolean save(Long officeId, Long userId);
    Collection<Long> getSavedOffices(Long userId);
}
