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
    private LocalDateTime createdDate;
}