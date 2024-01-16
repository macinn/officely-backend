package pw.react.backend.web;

import pw.react.backend.models.Saved;

import java.time.LocalDateTime;

public record SavedDto(
        long id,
        long userId,
        long officeId
) {
    public static SavedDto valueFrom(Saved saved) {
        return new SavedDto(
                saved.getId(),
                saved.getUserId(),
                saved.getOfficeId()
        );
    }
    public static Saved convertToSaved(SavedDto savedDto)
    {
        Saved saved = new Saved();
        saved.setId(savedDto.id());
        saved.setUserId(savedDto.userId());
        saved.setOfficeId(savedDto.officeId());
        saved.getCreationDate(LocalDateTime.now());
        return saved;
    }
}
