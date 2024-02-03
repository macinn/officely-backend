package pw.react.backend.services;

import org.springframework.web.multipart.MultipartFile;
import pw.react.backend.models.OfficePhoto;

import java.util.Optional;

public interface PhotoService {
    OfficePhoto storePhoto(String fileUrl, String fileName, long officeId, boolean isMain);
    Optional<OfficePhoto[]> getOfficePhotos(long officeId);
    Optional<OfficePhoto> getOfficeMainPhoto(long officeId);
    Optional<OfficePhoto> getPhoto(String photoId);
    void deleteOfficePhotos(long officeId);
    void deleteById(String photoId);
}
