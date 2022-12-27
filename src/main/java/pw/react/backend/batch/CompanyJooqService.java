package pw.react.backend.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.react.backend.dao.CompanyRepository;
import pw.react.backend.models.Company;
import pw.react.backend.services.CompanyMainService;

import java.util.Collection;
import java.util.Collections;

class CompanyJooqService extends CompanyMainService {
    private final Logger logger = LoggerFactory.getLogger(CompanyJooqService.class);

    private final BatchRepository<Company> batchRepository;

    CompanyJooqService(CompanyRepository repository, BatchRepository<Company> batchRepository) {
        super(repository);
        this.batchRepository = batchRepository;
    }

    @Override
    public Collection<Company> batchSave(Collection<Company> companies) {
        logger.info("Batch insert.");
        if (companies != null && !companies.isEmpty()) {
            return batchRepository.insertAll(companies);
        } else {
            logger.warn("Companies collection is empty or null.");
            return Collections.emptyList();
        }
    }
}
