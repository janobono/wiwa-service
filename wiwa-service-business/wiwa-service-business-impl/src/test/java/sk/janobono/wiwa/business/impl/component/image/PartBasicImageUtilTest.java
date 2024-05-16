package sk.janobono.wiwa.business.impl.component.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import sk.janobono.wiwa.business.model.order.part.PartBasicData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class PartBasicImageUtilTest {

    private ObjectMapper objectMapper;

    private PartBasicImageUtil partBasicImageUtil;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        partBasicImageUtil = new PartBasicImageUtil();
    }

    @ParameterizedTest
    @ValueSource(ints = {250, 500, 750, 1000, 1250, 1500, 1750, 2000, 2250})
    void generateImages_whenValidData_thenTheseResults(final int size) throws IOException {
        final PartBasicData part = objectMapper.readValue(getClass().getResource("/part_basic_%d_%d.json".formatted(size, size)), PartBasicData.class);
//        Files.write(Path.of("./target").resolve("basic_%d.png".formatted(size)), partBasicImageUtil.generateImage(part).data());
    }
}
