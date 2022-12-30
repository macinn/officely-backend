package pw.react.backend.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import pw.react.backend.dao.CompanyRepository;
import pw.react.backend.dao.UserRepository;

@Profile("!batch")
public class NonBatchConfig {

    @Bean
    public CompanyService companyService(CompanyRepository companyRepository) {
        return new CompanyMainService(companyRepository);
    }

    @Bean
    public UserService userService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return new UserMainService(userRepository, passwordEncoder);
    }

}
