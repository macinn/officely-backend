package pw.react.backend.models;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "saved")
public class Saved implements Serializable {
    @Serial
    private static final long serialVersionUID = -2245638451787316603L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private long userId;
    @Column
    private long officeId;
    @Column
    private LocalDateTime creationDate;

    public long getId() {
        return id;
    }
    public long getUserId() {
        return userId;
    }

    public long getOfficeId() {
        return officeId;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setOfficeId(long officeId) {
        this.officeId = officeId;
    }

    public void getCreationDate(LocalDateTime createdDate) {
        this.creationDate = createdDate;
    }
}