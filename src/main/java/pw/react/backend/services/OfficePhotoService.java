package pw.react.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pw.react.backend.dao.OfficePhotoRepository;
import pw.react.backend.exceptions.InvalidFileException;
import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.models.OfficePhoto;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

class OfficePhotoService implements PhotoService {

    private final Logger logger = LoggerFactory.getLogger(OfficePhotoService.class);

    private final OfficePhotoRepository repository;

    public OfficePhotoService(OfficePhotoRepository repository) {
        this.repository = repository;
    }

    @Override
    public OfficePhoto storePhoto(long companyId, MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new InvalidFileException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            OfficePhoto newOfficePhoto = new OfficePhoto(fileName, file.getContentType(), companyId, file.getBytes());
            return repository.save(newOfficePhoto);
        } catch (IOException ex) {
            throw new InvalidFileException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public Optional<OfficePhoto[]> getOfficePhotos(long companyId) {
        return Optional.ofNullable(repository.findByOfficeId(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with companyId " + companyId)));
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

}
