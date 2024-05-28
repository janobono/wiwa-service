package sk.janobono.wiwa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication(
        scanBasePackages = {"sk.janobono.wiwa"}
)
@ConfigurationPropertiesScan(basePackages = {"sk.janobono.wiwa"})
public class TestApplication {

    public static void main(final String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
