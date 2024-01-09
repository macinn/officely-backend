package pw.react.backend.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.react.backend.dao.ReservationRepository;
import pw.react.backend.models.Reservation;
import pw.react.backend.services.ReservationMainService;

public class ReservationBatchService extends ReservationMainService {
    private final Logger logger = LoggerFactory.getLogger(OfficeBatchService.class);
    private final BatchRepository<Reservation> batchRepository;
    ReservationBatchService(ReservationRepository repository, BatchRepository<Reservation> batchRepository) {
        super(repository);
        this.batchRepository = batchRepository;
    }
}
