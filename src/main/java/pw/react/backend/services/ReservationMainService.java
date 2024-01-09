package pw.react.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.react.backend.dao.ReservationRepository;

public class ReservationMainService implements ReservationService{
    private final Logger logger = LoggerFactory.getLogger(OfficeMainService.class);
    private ReservationRepository repository;
    protected ReservationMainService(ReservationRepository repository) {
        this.repository = repository;
    }
}
