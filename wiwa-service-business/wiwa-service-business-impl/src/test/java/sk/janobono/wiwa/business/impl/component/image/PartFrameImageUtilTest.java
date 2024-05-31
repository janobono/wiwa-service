package sk.janobono.wiwa.business.impl.component.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import sk.janobono.wiwa.business.model.application.OrderPropertiesData;
import sk.janobono.wiwa.business.model.order.OrderItemImageData;
import sk.janobono.wiwa.business.model.order.part.PartFrameData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PartFrameImageUtilTest {

    private ObjectMapper objectMapper;

    private PartFrameImageUtil partFrameImageUtil;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        partFrameImageUtil = new PartFrameImageUtil();
    }

    @ParameterizedTest
    @ValueSource(strings = {"part_frame", "part_frame_vertical"})
    void generateImages_whenValidData_thenTheseResults(final String name) throws IOException {
        final PartFrameData part = objectMapper.readValue(getClass().getResource("/%s.json".formatted(name)), PartFrameData.class);

        final List<OrderItemImageData> images = partFrameImageUtil.generateImages(
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

        assertThat(images).hasSize(5);

        for (final OrderItemImageData item : images) {
            Files.write(Path.of("./target").resolve("%s_%s.png".formatted(name, item.itemImage().name())),
                    item.image());
        }
    }
}
