package pw.react.backend.batch;

import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import pw.react.backend.dao.CompanyRepository;
import pw.react.backend.dao.UserRepository;
import pw.react.backend.models.Company;
import pw.react.backend.models.User;
import pw.react.backend.services.CompanyService;
import pw.react.backend.services.UserService;

class BatchConfig {

    @Value("${jooq.batchSize}")
    int batchSize;
    @Autowired
    private DefaultDSLContext dsl;
    @Bean
    public CompanyService companyService(CompanyRepository companyRepository, BatchRepository<Company> companyBatchRepository) {
        return new CompanyBatchService(companyRepository, companyBatchRepository);
    }

    @Bean
    public UserService userService(UserRepository userRepository, BatchRepository<User> userBatchRepository) {
        return new UserBatchService(userRepository, userBatchRepository);
    }

    @Bean
    public CompanyBatchRepository companyBatchRepository() {
        return new CompanyBatchRepository(dsl);
    }

    @Bean
    public UserBatchRepository userBatchRepository(DefaultDSLContext dsl) {
        return new UserBatchRepository(dsl);
    }
}
