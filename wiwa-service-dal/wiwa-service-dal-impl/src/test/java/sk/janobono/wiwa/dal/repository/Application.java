package sk.janobono.wiwa.dal.repository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(
        scanBasePackages = {"sk.janobono.wiwa.dal"}
)
@ConfigurationPropertiesScan(basePackages = {"sk.janobono.wiwa.dal"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
