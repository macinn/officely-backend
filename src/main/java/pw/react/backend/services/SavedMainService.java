package pw.react.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.react.backend.dao.SavedRepository;
import pw.react.backend.models.Saved;

import java.util.Optional;

public class SavedMainService implements SavedService{
    private final Logger log = LoggerFactory.getLogger(OfficeMainService.class);

    final private SavedRepository savedRepository;

    protected SavedMainService(SavedRepository repository) {
        this.savedRepository = repository;
    }

    @Override
    public boolean delete(Long officeId, Long userId) {
        Optional<Saved> saved = savedRepository.findByUserIdAndOfficeId(userId, officeId);
        if(saved.isPresent())
        {
            savedRepository.delete(saved.get());
            return true;
        }
        else {
            log.error("Saved with officeId {} and userId {} does not exist.", officeId, userId);
            return false;
        }
    }

    @Override
    public boolean save(Long officeId, Long userId) {
        Optional<Saved> saved = savedRepository.findByUserIdAndOfficeId(userId, officeId);
        if(saved.isEmpty())
        {
            Saved savedNew = new Saved();
            savedNew.setOfficeId(officeId);
            savedNew.setUserId(userId);
            savedRepository.save(savedNew);
        }
        return true;
    }

}
