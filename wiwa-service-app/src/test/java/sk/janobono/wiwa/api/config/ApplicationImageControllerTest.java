package sk.janobono.wiwa.api.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sk.janobono.wiwa.BaseIntegrationTest;
import sk.janobono.wiwa.api.model.ApplicationImageWeb;
import sk.janobono.wiwa.common.component.ImageUtil;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationImageControllerTest extends BaseIntegrationTest {

    @Autowired
    public ImageUtil imageUtil;

    @Test
    void fullTest() {
        final String token = signIn(DEFAULT_MANAGER, PASSWORD).token();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(token);

        final MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", new ByteArrayResource(imageUtil.generateMessageImage("test")) {
            @Override
            public String getFilename() {
                return "test.png";
            }
        });
        final HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(form, headers);

        final ResponseEntity<ApplicationImageWeb> uploadedImage = restTemplate.exchange(
                getURI("/config/application-images"),
                HttpMethod.POST,
                httpEntity,
                ApplicationImageWeb.class
        );
        assertThat(uploadedImage.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(uploadedImage.getBody()).isNotNull();
        assertThat(uploadedImage.hasBody()).isTrue();
        assertThat(uploadedImage.getBody().fileName()).isEqualTo("test.png");
        assertThat(uploadedImage.getBody().thumbnail().startsWith("data:" + MediaType.IMAGE_PNG_VALUE)).isTrue();

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        final Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "fileName", "fileType");
        final ResponseEntity<JsonNode> response = restTemplate.exchange(
                getURI("/config/application-images", pageableToParams(pageable)),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                JsonNode.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        final Page<ApplicationImageWeb> page = getPage(response.getBody(), pageable, ApplicationImageWeb.class);
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).fileName()).isEqualTo("test.png");
        assertThat(page.getContent().get(0).thumbnail().startsWith("data:" + MediaType.IMAGE_PNG_VALUE)).isTrue();
    }
}
