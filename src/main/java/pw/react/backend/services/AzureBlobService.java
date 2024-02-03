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
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
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

        String blobName = UUID.randomUUID().toString() + "_" + multipartFile.getOriginalFilename();
        BlobClient blob = blobContainerClient.getBlobClient(blobName);
        blob.upload(multipartFile.getInputStream(), multipartFile.getSize(), true);
        return blob.getBlobUrl();
    }

    public byte[] getFile(String fileName) throws URISyntaxException {

        BlobClient blob = blobContainerClient.getBlobClient(fileName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        blob.download(outputStream);
        final byte[] bytes = outputStream.toByteArray();
        return bytes;

    }

    public List<String> listBlobs() {

        PagedIterable<BlobItem> items = blobContainerClient.listBlobs();
        List<String> names = new ArrayList<String>();
        for (BlobItem item : items) {
            names.add(item.getName());
        }
        return names;

    }

    public Boolean deleteBlob(String blobName) {

        BlobClient blob = blobContainerClient.getBlobClient(blobName);
        blob.delete();
        return true;
    }

    public String uploadFromUrl(String photoUrl) throws IOException {
        String blobName = UUID.randomUUID().toString() + "_" + new File(photoUrl).getName();

        URL url = new URL(photoUrl);

        try (InputStream in = url.openStream()) {
            BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
            blobClient.upload(in, true);
            return blobClient.getBlobUrl();
        }
        catch (Exception e) {
            log.error("Error while uploading file from url");
        }
        return null;
    }
}