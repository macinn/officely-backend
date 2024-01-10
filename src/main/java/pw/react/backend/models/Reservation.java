package pw.react.backend.models;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
public class Reservation implements Serializable {
    @Serial
    private static final long serialVersionUID = -4822246351443240656L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private long userId;
    @Column
    private long officeId;
    @Column
    private LocalDateTime startDateTime;
    @Column
    private LocalDateTime endDateTime;

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public long getOfficeId() {
        return officeId;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
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

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }
}
