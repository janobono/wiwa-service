package sk.janobono.wiwa;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.component.LocalStorage;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.dal.domain.ApplicationPropertyDo;
import sk.janobono.wiwa.dal.domain.ApplicationPropertyKeyDo;
import sk.janobono.wiwa.dal.domain.AuthorityDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;
import sk.janobono.wiwa.dal.repository.AuthorityRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;
import sk.janobono.wiwa.model.Authority;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

@RequiredArgsConstructor
@Component
public class InitDataCommandLineRunner implements CommandLineRunner {

    private final ObjectMapper objectMapper;
    private final CommonConfigProperties commonConfigProperties;
    private final PasswordEncoder passwordEncoder;
    private final LocalStorage localStorage;
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
                final AuthorityDo authorityDo = new AuthorityDo();
                authorityDo.setAuthority(authority);
                authorityRepository.save(authorityDo);
            }
        }
    }

    private void initUsers() throws Exception {
        if (userRepository.count() == 0L) {
            final Set<AuthorityDo> authorities = new HashSet<>(authorityRepository.findAll());
            final UserDo userDo = new UserDo();
            userDo.setUsername("wiwa");
            userDo.setPassword(passwordEncoder.encode("wiwa"));
            userDo.setFirstName("wiwa");
            userDo.setLastName("wiwa");
            userDo.setEmail(commonConfigProperties.mail());
            userDo.setGdpr(true);
            userDo.setConfirmed(true);
            userDo.setEnabled(true);
            userDo.setAuthorities(authorities);
            userRepository.save(userDo);
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
                    applicationPropertyRepository.save(new ApplicationPropertyDo(
                            new ApplicationPropertyKeyDo(propertyId[0], propertyId[1]),
                            new String(localStorage.getFileData(dataFile.toPath()))
                    ));
                }
            }
        }
    }
}
