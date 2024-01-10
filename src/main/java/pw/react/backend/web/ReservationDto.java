package pw.react.backend.web;

import pw.react.backend.models.Reservation;

import java.time.LocalDateTime;

public record ReservationDto(
        long id,
        long userId,
        long officeId,
        LocalDateTime startDateTime,
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
