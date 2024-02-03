package pw.react.backend.controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pw.react.backend.exceptions.ResourceNotFoundException;
import pw.react.backend.models.Office;
import pw.react.backend.models.OfficePhoto;
import pw.react.backend.services.AzureBlobService;
import pw.react.backend.services.PhotoService;
import pw.react.backend.web.UploadFileResponse;


@RestController
@RequestMapping("/offices/{id}/photos")
public class PhotoController {

    @Autowired
    private AzureBlobService azureBlobAdapter;
    private PhotoService officePhotoService;
    @Autowired
    public void setOfficePhotoService(PhotoService officePhotoService) {
        this.officePhotoService = officePhotoService;
    }

    @PostMapping("/main")
    public ResponseEntity<String> uploadMain(@PathVariable Long id, @RequestParam MultipartFile file) throws IOException {

        String fileName = azureBlobAdapter.upload(file);
        if(fileName == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        officePhotoService.storePhoto(fileName, file.getOriginalFilename(), id, true);
        return ResponseEntity.ok(fileName);
    }



    @PostMapping()
    public ResponseEntity<String> upload(@PathVariable Long id, @RequestParam MultipartFile file) throws IOException {

        String fileName = azureBlobAdapter.upload(file);
        if(fileName == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        officePhotoService.storePhoto(fileName, file.getOriginalFilename(), id, false);
        return ResponseEntity.ok(fileName);
    }

    @PostMapping("/main/url")
    public ResponseEntity<String> uploadUrl(@PathVariable Long id, @RequestParam String fileUrl) throws IOException {

        String fileName = azureBlobAdapter.uploadFromUrl(fileUrl);
        if(fileName == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        officePhotoService.storePhoto(fileName, new File(fileUrl).getName(), id, true);
        return ResponseEntity.ok(fileName);
    }

    @PostMapping("/url")
    public ResponseEntity<String> uploadMainUrl(@PathVariable Long id, @RequestParam String fileUrl) throws IOException {

        String fileName = azureBlobAdapter.uploadFromUrl(fileUrl);
        if(fileName == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        officePhotoService.storePhoto(fileName, new File(fileUrl).getName(), id, false);
        return ResponseEntity.ok(fileName);
    }


//    @GetMapping
//    public ResponseEntity<List<String>> getAllBlobs() {
//
//        List<String> items = azureBlobAdapter.listBlobs();
//        return ResponseEntity.ok(items);
//    }

    // Photos
//    @Operation(summary = "Get main photo for company")
//    @GetMapping(value = "/{officeId}/thumbnail", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
//    public @ResponseBody byte[] getThumbnail(@RequestHeader HttpHeaders headers, @PathVariable Long officeId) {
//
//        Optional<Office> office = officeService.getById(officeId);
//        if(office.isPresent()) {
//            String thumbnailId = office.get().getOfficePhotoThumbnailId();
//            Optional<OfficePhoto> officeThumbnail = officePhotoService.getPhoto(thumbnailId);
//            return officeThumbnail.map(OfficePhoto::getData).orElseGet(() -> new byte[0]);
//        }
//        else
//            throw new ResourceNotFoundException(String.format("Office with %d does not exist", officeId));
//    }

//    @Operation(summary = "Upload main photo for company")
//    @PostMapping("/{officeId}/thumbnail")
//    public ResponseEntity<UploadFileResponse> uploadThumbnail(@RequestHeader HttpHeaders headers,
//                                                              @PathVariable Long officeId,
//                                                              @RequestParam(value="file") MultipartFile file) {
//
//        OfficePhoto officePhoto = officePhotoService.storePhoto(officeId, file);
//        Optional<Office> office = officeService.getById(officeId);
//
//        if(office.isPresent()) {
//            office.get().setOfficePhotoThumbnailId(officePhoto.getId());
//            officeService.updateOffice(officeId, office.get());
//            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                    .path("/companies/" + officeId + "/logo/")
//                    .path(officePhoto.getFileName())
//                    .toUriString();
//            UploadFileResponse response = new UploadFileResponse(
//                    officePhoto.getFileName(),
//                    fileDownloadUri,
//                    file.getContentType(),
//                    file.getSize()
//            );
//            return ResponseEntity.status(HttpStatus.CREATED).body(response);
//        }
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
//    }
//
//    @Operation(summary = "Get all photos of a company")
//    @GetMapping(value = "/{officeId}/photos", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
//    public @ResponseBody byte[][] getPhotos(@RequestHeader HttpHeaders headers, @PathVariable Long officeId) {
//        Optional<OfficePhoto[]> officePhotos = officePhotoService.getOfficePhotos(officeId);
//        return officePhotos.map(photos -> Arrays.stream(photos)
//                .map(OfficePhoto::getData).toArray(byte[][]::new)).orElseGet(() -> new byte[0][]);
//
//    }
//
//    @Operation(summary = "Upload photo for company")
//    @PostMapping("/{officeId}/photos")
//    public ResponseEntity<UploadFileResponse> uploadPhoto(@RequestHeader HttpHeaders headers,
//                                                          @PathVariable Long officeId,
//                                                          @RequestParam("file") MultipartFile file) {
//        OfficePhoto officePhoto = officePhotoService.storePhoto(officeId, file);
//
//        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/companies/" + officeId + "/logo/")
//                .path(officePhoto.getFileName())
//                .toUriString();
//        UploadFileResponse response = new UploadFileResponse(
//                officePhoto.getFileName(),
//                fileDownloadUri,
//                file.getContentType(),
//                file.getSize()
//        );
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }

//    @Operation(summary = "Delete photos for given company",
//            description = "Admin role required")
//    @DeleteMapping(value = "/{officeId}/photos/{photoId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void removePhotos    (@RequestHeader HttpHeaders headers, @PathVariable Long officeId, @PathVariable String photoId) {
//        officePhotoService.deleteById(photoId);
//    }
//
//    @DeleteMapping
//    public ResponseEntity<Boolean> delete(@RequestParam String fileName) {
//
//        azureBlobAdapter.deleteBlob(fileName);
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping("/download")
//    public ResponseEntity<Resource> getFile(@RequestParam String fileName) throws URISyntaxException {
//
//        ByteArrayResource resource = new ByteArrayResource(azureBlobAdapter.getFile(fileName));
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
//
//        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).headers(headers).body(resource);
//    }
}