package sk.janobono.wiwa.business.impl.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import sk.janobono.wiwa.business.TestRepositories;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PropertyUtilServiceTest {

    private PropertyUtilService propertyUtilService;

    @BeforeEach
    void setUp() {
        final ApplicationPropertyRepository applicationPropertyRepository = Mockito.mock(ApplicationPropertyRepository.class);
        new TestRepositories().mock(applicationPropertyRepository);
        propertyUtilService = new PropertyUtilService(applicationPropertyRepository);
    }

    @Test
    void fullTest() {
        propertyUtilService.setProperty("key1", "value1");
        propertyUtilService.setProperty(l -> Long.toString(l), "key2", 1L);
        final Optional<String> value1 = propertyUtilService.getProperty("key1");
        assertThat(value1.isPresent()).isTrue();
        assertThat(value1.get()).isEqualTo("value1");
        final Optional<Long> value2 = propertyUtilService.getProperty(Long::valueOf, "key2");
        assertThat(value2.isPresent()).isTrue();
        assertThat(value2.get()).isEqualTo(1L);
    }
}
