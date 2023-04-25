package sk.janobono.wiwa;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.common.component.LocalStorage;
import sk.janobono.wiwa.common.component.RandomString;
import sk.janobono.wiwa.common.config.CommonConfigProperties;
import sk.janobono.wiwa.common.model.Authority;
import sk.janobono.wiwa.dal.domain.ApplicationPropertyDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;
import sk.janobono.wiwa.dal.repository.AuthorityRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
public class InitDataCommandLineRunner implements CommandLineRunner {

    private final ObjectMapper objectMapper;
    private final CommonConfigProperties commonConfigProperties;
    private final PasswordEncoder passwordEncoder;
    private final LocalStorage localStorage;
    private final RandomString randomString;
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final ApplicationPropertyRepository applicationPropertyRepository;

    @Override
    public void run(final String... args) throws Exception {
        final Path dataDir = Path.of(commonConfigProperties.initDataPath()).normalize().toAbsolutePath();
        initAuthorities();
        initUsers(dataDir);
        initApplicationProperties(dataDir);
    }

    private void initAuthorities() {
        if (authorityRepository.count() == 0L) {
            log.debug("initAuthorities()");
            for (final Authority authority : Authority.values()) {
                authorityRepository.addAuthority(authority);
            }
        }
    }

    private void initUsers(final Path dataDir) throws Exception {
        if (userRepository.count() == 0L) {
            log.debug("initUsers({})", dataDir);
            final Path dataPath = dataDir.resolve("users.json");
            userRepository.addUser(
                    new UserDo(
                            null,
                            "wiwa",
                            passwordEncoder.encode("wiwa"),
                            null,
                            "wiwa",
                            null,
                            "wiwa",
                            null,
                            commonConfigProperties.mail(),
                            true,
                            true,
                            true,
                            Arrays.stream(Authority.values()).collect(Collectors.toSet())
                    )
            );
        }
    }

    private void initApplicationProperties(final Path dataDir) throws Exception {
        if (applicationPropertyRepository.count() == 0L) {
            log.debug("initApplicationProperties({})", dataDir);
            final Path dataPath = dataDir.resolve("application-properties.json");
            if (dataPath.toFile().exists()) {
                final ApplicationPropertyDo[] applicationProperties = objectMapper.readValue(
                        dataPath.toFile(), ApplicationPropertyDo[].class
                );
                for (final ApplicationPropertyDo applicationPropertyDo : applicationProperties) {
                    applicationPropertyRepository.addApplicationProperty(applicationPropertyDo);
                }
            }
            final Path dataDirPath = dataDir.resolve("application-properties");
            if (dataDirPath.toFile().exists() && dataDirPath.toFile().isDirectory()) {
                final List<File> dataFiles = Arrays.stream(Objects.requireNonNull(dataDirPath.toFile().listFiles())).filter(
                        f -> f.isFile() && f.getName().endsWith(".md")
                ).toList();
                for (final File dataFile : dataFiles) {
                    final String[] propertyId = dataFile.getName().replaceAll(".md", "").split("-");
                    applicationPropertyRepository.addApplicationProperty(
                            new ApplicationPropertyDo(
                                    propertyId[0], propertyId[1], propertyId[2],
                                    new String(localStorage.getFileData(dataFile.toPath()))
                            )
                    );
                }
            }
        }
    }
}
