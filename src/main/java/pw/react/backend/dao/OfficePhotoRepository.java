package pw.react.backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import pw.react.backend.models.OfficePhoto;

import java.util.Optional;

@Transactional
public interface OfficePhotoRepository extends JpaRepository<OfficePhoto, String> {
    Optional<OfficePhoto> findById(String id);
    Optional<OfficePhoto[]> findByOfficeId(long officeId);
    void deleteByOfficeId(long companyId);
}
