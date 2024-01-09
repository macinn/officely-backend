package pw.react.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pw.react.backend.services.ReservationService;

@RestController
@RequestMapping(path = UserController.USERS_PATH)
public class ReservationController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    static final String USERS_PATH = "/reservations";
    private final ReservationService reservationService;
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

}
