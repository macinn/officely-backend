package pw.react.backend.web;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import pw.react.backend.models.Reservation;
import pw.react.backend.utils.JsonDateDeserializer;
import pw.react.backend.utils.JsonDateSerializer;

import java.time.LocalDateTime;

public record ReservationDto(
        long id,
        long userId,
        long officeId,
        @JsonDeserialize(using = JsonDateDeserializer.class) @JsonSerialize(using = JsonDateSerializer.class)
        LocalDateTime startDateTime,
        @JsonDeserialize(using = JsonDateDeserializer.class) @JsonSerialize(using = JsonDateSerializer.class)
        LocalDateTime endDateTime
) {
    public static ReservationDto valueFrom(pw.react.backend.models.Reservation reservation) {
        return new ReservationDto(
                reservation.getId(),
                reservation.getUserId(),
                reservation.getOfficeId(),
                reservation.getStartDateTime(),
                reservation.getEndDateTime()
        );
    }
    public static Reservation convertToReservation(ReservationDto reservationDto)
    {
        Reservation reservation = new Reservation();
        reservation.setId(reservationDto.id());
        reservation.setUserId(reservationDto.userId());
        reservation.setOfficeId(reservationDto.officeId());
        reservation.setStartDateTime(reservationDto.startDateTime());
        reservation.setEndDateTime(reservationDto.endDateTime());
        return reservation;
    }
}
