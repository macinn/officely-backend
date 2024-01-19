package pw.react.backend.services;

public interface SavedService {
    boolean delete(Long officeId, Long userId);
    boolean save(Long officeId, Long userId);
}
