package sk.janobono.wiwa.api;

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

class ApplicationImageManagementControllerIT extends BaseIntegrationTest {

    @Autowired
    public ImageUtil imageUtil;

    @Test
    void fullTest() {
        String token = signIn(DEFAULT_MANAGER, PASSWORD).token();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(token);

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", new ByteArrayResource(imageUtil.generateMessageImage("test")) {
            @Override
            public String getFilename() {
                return "test.png";
            }
        });
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(form, headers);

        ResponseEntity<ApplicationImageWeb> uploadedImage = restTemplate.exchange(
                getURI("/ui-management/application-images"),
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

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "fileName", "fileType");
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                getURI("/ui-management/application-images", pageableToParams(pageable)),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                JsonNode.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        Page<ApplicationImageWeb> page = getPage(response.getBody(), pageable, ApplicationImageWeb.class);
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).fileName()).isEqualTo("test.png");
        assertThat(page.getContent().get(0).thumbnail().startsWith("data:" + MediaType.IMAGE_PNG_VALUE)).isTrue();
    }
}
