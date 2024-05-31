package sk.janobono.wiwa.business.impl.component.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.janobono.wiwa.business.model.application.OrderPropertiesData;
import sk.janobono.wiwa.business.model.order.OrderItemImageData;
import sk.janobono.wiwa.business.model.order.part.PartDuplicatedFrameData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PartDuplicatedFrameImageUtilTest {

    private ObjectMapper objectMapper;

    private PartDuplicatedFrameImageUtil partDuplicatedFrameImageUtil;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        partDuplicatedFrameImageUtil = new PartDuplicatedFrameImageUtil();
    }

    @Test
    void generateImages_whenValidData_thenTheseResults() throws IOException {
        final PartDuplicatedFrameData part = objectMapper.readValue(getClass().getResource("/part_duplicated_frame.json"), PartDuplicatedFrameData.class);

        final List<OrderItemImageData> images = partDuplicatedFrameImageUtil.generateImages(
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

        assertThat(images).hasSize(6);

        for (final OrderItemImageData item : images) {
            Files.write(Path.of("./target").resolve("duplicated_frame_%s.png".formatted(item.itemImage().name())),
                    item.image());
        }
    }
}
