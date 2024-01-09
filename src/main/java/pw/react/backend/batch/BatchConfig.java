package pw.react.backend.batch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import pw.react.backend.dao.OfficeRepository;
import pw.react.backend.dao.ReservationRepository;
import pw.react.backend.dao.UserRepository;
import pw.react.backend.models.Office;
import pw.react.backend.models.Reservation;
import pw.react.backend.models.User;
import pw.react.backend.services.OfficeService;
import pw.react.backend.services.ReservationService;
import pw.react.backend.services.UserService;

import javax.sql.DataSource;

@Profile({"batch", "*mysql*"})
public class BatchConfig {

    private final DataSource dataSource;

    public BatchConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public UserService userService(UserRepository userRepository, PasswordEncoder passwordEncoder, BatchRepository<User> userBatchRepository) {
        return new UserBatchService(userRepository, passwordEncoder, userBatchRepository);
    }
    @Bean
    public OfficeService officeService(OfficeRepository officeRepository, BatchRepository<Office> officeBatchRepository) {
        return new OfficeBatchService(officeRepository, officeBatchRepository);
    }
    @Bean
    public ReservationService reservationService(ReservationRepository reservationRepository, BatchRepository<Reservation> reservationBatchRepository) {
        return new ReservationBatchService(reservationRepository, reservationBatchRepository);
    }
    @Bean
    public UserBatchRepository userBatchRepository(JdbcTemplate jdbcTemplate) {
        return new UserBatchRepository(jdbcTemplate);
    }
    @Bean
    public OfficeBatchRepository officeBatchRepository(JdbcTemplate jdbcTemplate) {
        return new OfficeBatchRepository(jdbcTemplate);
    }
    @Bean
    public ReservationBatchRepository reservationBatchRepository(JdbcTemplate jdbcTemplate) {
        return new ReservationBatchRepository(jdbcTemplate);
    }

}
