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
    @Column
    private String url;
    @Column
    private String fileName;
    @Column
    private boolean isMain;
    @Column
    private long officeId;

    public OfficePhoto() {
    }

    public OfficePhoto(String url, String fileName, long officeId, boolean isMain) {
        this.url = url;
        this.fileName = fileName;
        this.officeId = officeId;
        this.isMain = isMain;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public long getOfficeId() {
        return officeId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setOfficeId(long officeId) {
        this.officeId = officeId;
    }

    public void setMain(boolean main) {
        isMain = main;
    }
}
