package sk.janobono.wiwa.business.impl.component.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.janobono.wiwa.business.model.order.part.PartFrameData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    @Test
    void generateImages_whenValidData_thenTheseResults() throws IOException {
        final PartFrameData part = objectMapper.readValue(getClass().getResource("/part_frame.json"), PartFrameData.class);
//        Files.write(Path.of("./target").resolve("frame.png"), partFrameImageUtil.generateImages(part).data());
    }
}
