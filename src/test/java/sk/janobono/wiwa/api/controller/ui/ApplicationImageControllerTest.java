package sk.janobono.wiwa.api.controller.ui;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sk.janobono.wiwa.BaseIntegrationTest;
import sk.janobono.wiwa.component.ImageUtil;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationImageControllerTest extends BaseIntegrationTest {

    @Autowired
    public ImageUtil imageUtil;

    @Test
    void fullTest() {
        final byte[] data = restTemplate.getForObject(
                getURI("/ui/application-images/{fileName}", Collections.singletonMap("fileName", "test.png")),
                byte[].class
        );
        assertThat(data).isNotNull();
        assertThat(data).isEqualTo(imageUtil.generateMessageImage(null));
    }
}
