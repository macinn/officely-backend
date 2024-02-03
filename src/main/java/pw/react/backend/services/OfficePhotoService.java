package pw.react.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.react.backend.dao.OfficePhotoRepository;
import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.models.OfficePhoto;

import java.util.Optional;

class OfficePhotoService implements PhotoService {

    private final Logger logger = LoggerFactory.getLogger(OfficePhotoService.class);

    private final OfficePhotoRepository repository;

    public OfficePhotoService(OfficePhotoRepository repository) {
        this.repository = repository;
    }

    @Override
    public OfficePhoto storePhoto(String fileUrl, String fileName, long companyId, boolean isMain) {
        OfficePhoto newOfficePhoto = new OfficePhoto(fileUrl, fileName, companyId, isMain);
        if(isMain)
        {
            repository.findByOfficeIdAndIsMain(companyId, true).ifPresent(photo -> {
                photo.setMain(false);
                repository.save(photo);
            });
        }
        return repository.save(newOfficePhoto);
    }

    @Override
    public Optional<OfficePhoto[]> getOfficePhotos(long companyId) {
        return repository.findByOfficeId(companyId);
    }

    @Override
    public Optional<OfficePhoto> getOfficeMainPhoto(long officeId) {
        return repository.findByOfficeIdAndIsMain(officeId, true);
    }

    @Override
    public Optional<OfficePhoto> getPhoto(String photoId) {
        return Optional.ofNullable(repository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with photoId " + photoId)));
    }

    @Override
    public void deleteOfficePhotos(long companyId) {
        repository.deleteByOfficeId(companyId);
        logger.info("Logo for the company with id {} deleted.", companyId);
    }

    @Override
    public void deleteById(String photoId) {
        repository.deleteById(photoId);
        logger.info("Photo with id {} deleted.", photoId);
    }

}
