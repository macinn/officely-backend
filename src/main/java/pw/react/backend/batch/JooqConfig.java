package pw.react.backend.batch;

import org.jooq.*;
import org.jooq.impl.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pw.react.backend.dao.CompanyRepository;
import pw.react.backend.services.CompanyService;

import javax.sql.DataSource;

@Configuration
@ComponentScan({"pw.react.backend.models.backend.tables"})
@EnableTransactionManagement
@Profile({"batch", "mysql*"})
public class JooqConfig {
    private final DataSource dataSource;
    private final String sqlDialectName;

    public JooqConfig(DataSource dataSource, @Value("${spring.jooq.sql-dialect}") String sqlDialectName) {
        this.dataSource = dataSource;
        this.sqlDialectName = sqlDialectName;
    }

    @Bean
    public CompanyService companyService(CompanyRepository companyRepository, DataSourceConnectionProvider connectionProvider) {
        return new CompanyJooqService(companyRepository, companyBatchRepository(connectionProvider));
    }

    @Bean
    public DataSourceConnectionProvider connectionProvider() {
        return new DataSourceConnectionProvider(dataSource);
    }

    @Bean
    public ExceptionTranslator exceptionTransformer() {
        return new ExceptionTranslator();
    }

    @Bean
    public DefaultDSLContext dsl() {
        return new DefaultDSLContext(configuration());
    }

    @Bean
    public DefaultConfiguration configuration() {
        DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
        jooqConfiguration.set(connectionProvider());
        jooqConfiguration.set(new DefaultExecuteListenerProvider(exceptionTransformer()));

        SQLDialect dialect = SQLDialect.valueOf(sqlDialectName);
        jooqConfiguration.set(dialect);

        return jooqConfiguration;
    }

    @Bean
    public CompanyBaseRepository companyBatchRepository(DataSourceConnectionProvider connectionProvider) {
        return new CompanyBaseRepository(dsl());
    }

    private static class ExceptionTranslator implements ExecuteListener {
        public void exception(ExecuteContext context) {
            SQLDialect dialect = context.configuration().dialect();
            SQLExceptionTranslator translator = new SQLErrorCodeSQLExceptionTranslator(dialect.name());
            context.exception(translator.translate("Access database using Jooq", context.sql(), context.sqlException()));
        }
    }
}
