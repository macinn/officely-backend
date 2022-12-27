package pw.react.backend.services;

import org.springframework.context.annotation.*;
import pw.react.backend.batch.JooqConfig;
import pw.react.backend.dao.CompanyRepository;

@Configuration
@Import(JooqConfig.class)
@Profile("!batch")
public class NonBatchConfig {

    @Bean
    public CompanyService companyService(CompanyRepository companyRepository) {
        return new CompanyMainService(companyRepository);
    }

}
