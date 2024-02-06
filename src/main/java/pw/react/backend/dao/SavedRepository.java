package pw.react.backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pw.react.backend.models.Saved;

import java.util.Optional;

public interface SavedRepository extends JpaRepository<Saved, Long> {
    Optional<Saved> findByUserIdAndOfficeId(Long userId, Long officeId);
    Optional<Saved> findByUserId(Long userId);
}
