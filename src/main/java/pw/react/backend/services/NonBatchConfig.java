package pw.react.backend.services;

import org.springframework.context.annotation.*;
import pw.react.backend.batch.JooqConfig;
import pw.react.backend.dao.CompanyRepository;
import pw.react.backend.dao.UserRepository;

@Configuration
@Import(JooqConfig.class)
@Profile("!batch")
public class NonBatchConfig {

    @Bean
    public CompanyService companyService(CompanyRepository companyRepository) {
        return new CompanyMainService(companyRepository);
    }

    @Bean
    public UserService userService(UserRepository userRepository) {
        return new UserMainService(userRepository);
    }

}
