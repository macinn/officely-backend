package pw.react.backend.models;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serial;

@Entity
@Table(name = "office_photo")
public class OfficePhoto {
    @Serial
    private static final long serialVersionUID = -5807397203914552373L;
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String fileName;
    private String fileType;
    private long officeId;
    @Lob
    @Column(length = 200000000)
    private byte[] data;

    public OfficePhoto() {
    }

    public OfficePhoto(String fileName, String fileType, long officeId, byte[] data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.officeId = officeId;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(long officeId) {
        this.officeId = officeId;
    }
}
