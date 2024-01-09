package pw.react.backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pw.react.backend.models.Office;

public interface OfficeRepository extends JpaRepository<Office, Long> {
}
