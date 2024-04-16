package sk.janobono.wiwa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.r3n.jdbc.PostgreSqlBuilder;
import sk.r3n.jdbc.SqlBuilder;

@Configuration
public class R3nConfig {

    @Bean
    public SqlBuilder sqlBuilder() {
        return new PostgreSqlBuilder(false);
    }
}
