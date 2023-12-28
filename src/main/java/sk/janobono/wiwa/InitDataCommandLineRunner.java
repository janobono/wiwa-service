package sk.janobono.wiwa;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.dal.domain.ApplicationPropertyDo;
import sk.janobono.wiwa.dal.domain.AuthorityDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;
import sk.janobono.wiwa.dal.repository.AuthorityRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;
import sk.janobono.wiwa.model.Authority;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class InitDataCommandLineRunner implements CommandLineRunner {

    private final ObjectMapper objectMapper;
    private final CommonConfigProperties commonConfigProperties;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final ApplicationPropertyRepository applicationPropertyRepository;

    @Override
    public void run(final String... args) throws Exception {
        final Path dataDir = Path.of(commonConfigProperties.initDataPath()).normalize().toAbsolutePath();
        initAuthorities();
        initUsers();
        initApplicationProperties(dataDir);
    }

    private void initAuthorities() {
        if (authorityRepository.count() == 0L) {
            for (final Authority authority : Authority.values()) {
                authorityRepository.save(AuthorityDo.builder().authority(authority).build());
            }
        }
    }

    private void initUsers() throws Exception {
        if (userRepository.count() == 0L) {
            final UserDo userDo = userRepository.save(UserDo.builder()
                    .username("wiwa")
                    .password(passwordEncoder.encode("wiwa"))
                    .firstName("wiwa")
                    .lastName("wiwa")
                    .email(commonConfigProperties.mail())
                    .gdpr(true)
                    .confirmed(true)
                    .enabled(true)
                    .build());
            authorityRepository.saveUserAuthorities(userDo.getId(), authorityRepository.findAll().stream().map(AuthorityDo::getAuthority).toList());
        }
    }

    private void initApplicationProperties(final Path dataDir) throws Exception {
        if (applicationPropertyRepository.count() == 0L) {
            final Path dataPath = dataDir.resolve("application-properties.json");
            if (dataPath.toFile().exists()) {
                final ApplicationPropertyDo[] applicationProperties = objectMapper
                        .readValue(dataPath.toFile(), ApplicationPropertyDo[].class);
                applicationPropertyRepository.saveAll(Arrays.asList(applicationProperties));
            }
            final Path dataDirPath = dataDir.resolve("application-properties");
            if (dataDirPath.toFile().exists() && dataDirPath.toFile().isDirectory()) {
                final List<File> dataFiles = Arrays.stream(Objects.requireNonNull(dataDirPath.toFile().listFiles()))
                        .filter(f -> f.isFile() && f.getName().endsWith(".md"))
                        .toList();
                for (final File dataFile : dataFiles) {
                    final String[] propertyId = dataFile.getName().replaceAll(".md", "").split("-");
                    applicationPropertyRepository.save(ApplicationPropertyDo.builder()
                            .group(propertyId[0])
                            .key(propertyId[1])
                            .value(new String(getFileData(dataFile.toPath()))
                            ).build()
                    );
                }
            }
        }
    }

    private byte[] getFileData(final Path file) {
        try (
                final FileInputStream is = new FileInputStream(file.toFile());
                final DataInputStream di = new DataInputStream(is)
        ) {
            return di.readAllBytes();
        } catch (final Exception e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }
}
