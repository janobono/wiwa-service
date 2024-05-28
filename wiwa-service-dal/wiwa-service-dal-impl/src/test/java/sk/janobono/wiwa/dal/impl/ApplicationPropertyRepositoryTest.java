package sk.janobono.wiwa.dal.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sk.janobono.wiwa.dal.domain.ApplicationPropertyDo;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationPropertyRepositoryTest extends BaseRepositoryTest {

    @Autowired
    public ApplicationPropertyRepository applicationPropertyRepository;

    @Test
    void fullTest() {
        final ApplicationPropertyDo created = applicationPropertyRepository.save(
                ApplicationPropertyDo.builder()
                        .key("key")
                        .value("value")
                        .build()
        );
        assertThat(created.getKey()).isEqualTo("key");
        assertThat(created.getValue()).isEqualTo("value");

        final ApplicationPropertyDo modified = applicationPropertyRepository.save(
                ApplicationPropertyDo.builder()
                        .key("key")
                        .value("value2")
                        .build()
        );
        assertThat(modified.getKey()).isEqualTo("key");
        assertThat(modified.getValue()).isEqualTo("value2");

        Optional<ApplicationPropertyDo> searchResult = applicationPropertyRepository.findByKey("key");
        assertThat(searchResult.isPresent()).isTrue();
        assertThat(searchResult.get()).usingRecursiveComparison().isEqualTo(modified);

        applicationPropertyRepository.deleteByKey("key");

        searchResult = applicationPropertyRepository.findByKey("key");
        assertThat(searchResult.isEmpty()).isTrue();
    }
}
