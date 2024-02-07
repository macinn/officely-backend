package pw.react.backend.services;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class AzureBlobService {
    private final Logger log = LoggerFactory.getLogger(AzureBlobService.class);
    @Autowired
    BlobServiceClient blobServiceClient;

    @Autowired
    BlobContainerClient blobContainerClient;

    public String upload(MultipartFile multipartFile) throws IOException {

        String blobName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
        BlobClient blob = blobContainerClient.getBlobClient(blobName);
        blob.upload(multipartFile.getInputStream(), multipartFile.getSize(), true);
        return blob.getBlobUrl();
    }

    public Boolean deleteBlob(String blobName) {

        BlobClient blob = blobContainerClient.getBlobClient(blobName);
        blob.delete();
        return true;
    }

    public String uploadFromUrl(String photoUrl) throws IOException {
        String blobName = UUID.randomUUID().toString() + "_" + new File(photoUrl).getName() + ".jpeg";

        try {
            URL url = new URL(photoUrl);
            String path = url.getPath();
            Path filePath = Paths.get(path);
            blobName = UUID.randomUUID().toString() + "_" + filePath.getFileName().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        URL url = new URL(photoUrl);
        try {
            HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
            httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");

            BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
            blobClient.upload(httpcon.getInputStream(), true);
            return blobClient.getBlobUrl();
        }
        catch (Exception e) {
            log.error("Error while uploading file from url " + e.getMessage());
        }
        return null;
    }
}