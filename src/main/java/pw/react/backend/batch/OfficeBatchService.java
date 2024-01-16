package pw.react.backend.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.react.backend.dao.OfficeRepository;
import pw.react.backend.models.Office;
import pw.react.backend.services.OfficeMainService;

public class OfficeBatchService extends OfficeMainService {
    private final Logger logger = LoggerFactory.getLogger(OfficeBatchService.class);
    private final BatchRepository<Office> batchRepository;

    OfficeBatchService(OfficeRepository repository, BatchRepository<Office> batchRepository) {
        super(repository);
        this.batchRepository = batchRepository;
    }
}
