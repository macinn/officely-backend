package pw.react.backend.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String value;
    private LocalDateTime created;

    public Token() {}

    public Token(String value) {
        this.value = value;
        created = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }
}
