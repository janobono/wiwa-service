package sk.janobono.wiwa.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sk.janobono.wiwa.BaseIntegrationTest;
import sk.janobono.wiwa.common.component.ImageUtil;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationImageControllerIT extends BaseIntegrationTest {

    @Autowired
    public ImageUtil imageUtil;

    @Test
    void fullTest() {
        byte[] data = restTemplate.getForObject(
                getURI("/ui/application-images/{fileName}", Collections.singletonMap("fileName", "test.png")),
                byte[].class
        );
        assertThat(data).isNotNull();
        assertThat(data).isEqualTo(imageUtil.generateMessageImage(null));
    }
}
