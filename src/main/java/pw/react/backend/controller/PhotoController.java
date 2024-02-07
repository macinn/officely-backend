package pw.react.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pw.react.backend.services.AzureBlobService;
import pw.react.backend.services.PhotoService;

import java.io.File;
import java.io.IOException;


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
    @Operation(summary = "Upload main photo for company",
            description = "Admin role required")
    @CrossOrigin
    public ResponseEntity<String> uploadMain(@PathVariable Long id, @RequestParam MultipartFile file) throws IOException {

        String fileName = azureBlobAdapter.upload(file);
        if(fileName == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        officePhotoService.storePhoto(fileName, file.getOriginalFilename(), id, true);
        return ResponseEntity.ok(fileName);
    }

    @PostMapping()
    @Operation(summary = "Upload photo for company",
            description = "Admin role required")
    @CrossOrigin
    public ResponseEntity<String> upload(@PathVariable Long id, @RequestParam MultipartFile file) throws IOException {

        String fileName = azureBlobAdapter.upload(file);
        if(fileName == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        officePhotoService.storePhoto(fileName, file.getOriginalFilename(), id, false);
        return ResponseEntity.ok(fileName);
    }

    @PostMapping("/main/url")
    @Operation(summary = "Upload main photo for company using url",
            description = "Admin role required")
    @CrossOrigin
    public ResponseEntity<String> uploadUrl(@PathVariable Long id, @RequestBody UrlBody body) throws IOException {

        String fileName = azureBlobAdapter.uploadFromUrl(body.fileUrl);
        if(fileName == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        officePhotoService.storePhoto(fileName, new File(body.fileUrl).getName(), id, true);
        return ResponseEntity.ok(fileName);
    }

    @PostMapping("/url")
    @Operation(summary = "Upload photo for company using url",
            description = "Admin role required")
    @CrossOrigin
    public ResponseEntity<String> uploadMainUrl(@PathVariable Long id, @RequestBody UrlBody body) throws IOException {

        String fileName = azureBlobAdapter.uploadFromUrl(body.fileUrl);
        if(fileName == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        officePhotoService.storePhoto(fileName, new File(body.fileUrl).getName(), id, false);
        return ResponseEntity.ok(fileName);
    }

    @DeleteMapping
    @CrossOrigin
    public ResponseEntity<String> deleteByUrl(@PathVariable Long id, @RequestBody UrlBody body)
    {
        officePhotoService.deleteByUrl(id, body.fileUrl);
        return ResponseEntity.ok(body.fileUrl);
    }

    public static class UrlBody {
        private String fileUrl;

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }
    }

}