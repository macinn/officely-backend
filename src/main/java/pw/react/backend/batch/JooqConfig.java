package pw.react.backend.batch;

import org.jooq.*;
import org.jooq.conf.Settings;
import org.jooq.impl.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@ComponentScan({"pw.react.backend.models.backend.tables"})
@EnableTransactionManagement
@Profile({"batch", "mysql*"})
@Import(BatchConfig.class)
public class JooqConfig {
    private final DataSource dataSource;
    private final String sqlDialectName;
    private final int batchSize;

    public JooqConfig(DataSource dataSource, @Value("${spring.jooq.sql-dialect}") String sqlDialectName, @Value("${jooq.batchSize}") int batchSize) {
        this.dataSource = dataSource;
        this.sqlDialectName = sqlDialectName;
        this.batchSize = batchSize;
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
    public org.jooq.Configuration configuration() {
        return new DefaultConfiguration()
                .set(connectionProvider())
                .set(new DefaultExecuteListenerProvider(exceptionTransformer()))
                .set(SQLDialect.valueOf(sqlDialectName))
                .set(new Settings().withBatchSize(batchSize));
    }

    private static class ExceptionTranslator implements ExecuteListener {
        public void exception(ExecuteContext context) {
            SQLDialect dialect = context.configuration().dialect();
            SQLExceptionTranslator translator = new SQLErrorCodeSQLExceptionTranslator(dialect.name());
            context.exception(translator.translate("Access database using Jooq", context.sql(), context.sqlException()));
        }
    }
}
