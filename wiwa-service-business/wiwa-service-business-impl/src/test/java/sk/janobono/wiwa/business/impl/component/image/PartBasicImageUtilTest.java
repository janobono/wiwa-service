package sk.janobono.wiwa.business.impl.component.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import sk.janobono.wiwa.business.model.application.OrderPropertiesData;
import sk.janobono.wiwa.business.model.order.OrderItemImageData;
import sk.janobono.wiwa.business.model.order.part.PartBasicData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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

        final List<OrderItemImageData> images = partBasicImageUtil.generateImages(
                new OrderPropertiesData(
                        Map.of(),
                        Map.of(),
                        Map.of(),
                        Map.of(),
                        Map.of(),
                        Map.of(),
                        Map.of(),
                        "",
                        Map.of(),
                        Map.of()
                ),
                part
        );

        assertThat(images.size()).isEqualTo(2);

        for (final OrderItemImageData item : images) {
            Files.write(Path.of("./target").resolve("basic_%s_%d.png".formatted(item.itemImage().name(), size)),
                    item.image());
        }
    }
}
