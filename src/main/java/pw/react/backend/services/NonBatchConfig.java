package pw.react.backend.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import pw.react.backend.dao.OfficeRepository;
import pw.react.backend.dao.ReservationRepository;
import pw.react.backend.dao.UserRepository;

@Profile("!batch")
public class NonBatchConfig {
    @Bean
    public UserService userService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return new UserMainService(userRepository, passwordEncoder);
    }
    @Bean
    public ReservationService reservationService(ReservationRepository reservationRepository) {
        return new ReservationMainService(reservationRepository);
    }
//    @Bean
//    public OfficeService officeService(OfficeRepository officeRepository) {
//        return new OfficeMainService(officeRepository);
//    }
}
