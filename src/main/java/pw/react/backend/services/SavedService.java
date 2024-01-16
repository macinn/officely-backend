package pw.react.backend.services;

import pw.react.backend.models.Saved;

public interface SavedService {
    boolean delete(Long officeId, Long userId);
    Saved save(Saved saved);
}
