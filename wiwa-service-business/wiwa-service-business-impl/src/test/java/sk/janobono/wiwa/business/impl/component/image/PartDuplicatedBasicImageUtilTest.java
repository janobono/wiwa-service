package sk.janobono.wiwa.business.impl.component.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.janobono.wiwa.business.model.application.OrderPropertiesData;
import sk.janobono.wiwa.business.model.order.OrderItemImageData;
import sk.janobono.wiwa.business.model.order.part.PartDuplicatedBasicData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PartDuplicatedBasicImageUtilTest {

    private ObjectMapper objectMapper;

    private PartDuplicatedBasicImageUtil partDuplicatedBasicImageUtil;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        partDuplicatedBasicImageUtil = new PartDuplicatedBasicImageUtil();
    }

    @Test
    void generateImages_whenValidData_thenTheseResults() throws IOException {
        final PartDuplicatedBasicData part = objectMapper.readValue(getClass().getResource("/part_duplicated.json"), PartDuplicatedBasicData.class);

        final List<OrderItemImageData> images = partDuplicatedBasicImageUtil.generateImages(
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

        assertThat(images).hasSize(3);

        for (final OrderItemImageData item : images) {
            Files.write(Path.of("./target").resolve("duplicated_basic_%s.png".formatted(item.itemImage().name())),
                    item.image());
        }
    }
}
