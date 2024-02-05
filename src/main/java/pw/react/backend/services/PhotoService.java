package pw.react.backend.services;

import pw.react.backend.models.OfficePhoto;

import java.util.Optional;

public interface PhotoService {
    OfficePhoto storePhoto(String fileUrl, String fileName, long officeId, boolean isMain);
    Optional<OfficePhoto[]> getOfficePhotos(long officeId);
    Optional<OfficePhoto> getOfficeMainPhoto(long officeId);
    void deleteByUrl(long officeId, String photoUrl);
    Optional<OfficePhoto> getPhoto(String photoId);
    void deleteOfficePhotos(long officeId);
}
