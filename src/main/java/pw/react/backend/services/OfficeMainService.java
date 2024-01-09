package pw.react.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.react.backend.dao.OfficeRepository;

public class OfficeMainService implements OfficeService{
    private final Logger logger = LoggerFactory.getLogger(OfficeMainService.class);
    private OfficeRepository repository;
    protected OfficeMainService(OfficeRepository repository) {
        this.repository = repository;
    }
}
