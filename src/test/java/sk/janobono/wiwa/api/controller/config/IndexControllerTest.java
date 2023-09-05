package sk.janobono.wiwa.api.controller.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sk.janobono.wiwa.BaseIntegrationTest;
import sk.janobono.wiwa.api.model.ApplicationImageWeb;
import sk.janobono.wiwa.api.model.SingleValueBody;
import sk.janobono.wiwa.business.model.ui.ApplicationInfoItemSo;
import sk.janobono.wiwa.business.model.ui.ApplicationInfoSo;
import sk.janobono.wiwa.business.model.ui.CompanyInfoSo;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class IndexControllerTest extends BaseIntegrationTest {

    @Autowired
    public ImageUtil imageUtil;

    @Autowired
    public ApplicationPropertyRepository applicationPropertyRepository;

    @Test
    void fullTest() {
        final String token = signIn(DEFAULT_MANAGER, PASSWORD).token();

        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        // logo
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        final MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", new ByteArrayResource(imageUtil.generateMessageImage("test")) {
            @Override
            public String getFilename() {
                return "test.png";
            }
        });
        final HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(form, headers);

        final ResponseEntity<ApplicationImageWeb> uploadedImage = restTemplate.exchange(
                getURI("/config/logo"),
                HttpMethod.POST,
                httpEntity,
                ApplicationImageWeb.class
        );
        assertThat(uploadedImage.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(uploadedImage.getBody()).isNotNull();
        assertThat(uploadedImage.hasBody()).isTrue();
        assertThat(uploadedImage.getBody().fileName()).isEqualTo("logo.png");
        assertThat(uploadedImage.getBody().thumbnail().startsWith("data:" + MediaType.IMAGE_PNG_VALUE)).isTrue();

        // title
        headers.setContentType(MediaType.APPLICATION_JSON);
        final ResponseEntity<JsonNode> title = restTemplate.exchange(
                getURI("/config/title"),
                HttpMethod.POST,
                new HttpEntity<>(
                        new SingleValueBody<>("test"),
                        headers
                ),
                JsonNode.class
        );
        assertThat(title.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(title.getBody()).get("value").asText()).isEqualTo("test");

        // welcome-text
        final ResponseEntity<JsonNode> welcomeText = restTemplate.exchange(
                getURI("/config/welcome-text"),
                HttpMethod.POST,
                new HttpEntity<>(
                        new SingleValueBody<>("test"),
                        headers
                ),
                JsonNode.class
        );
        assertThat(welcomeText.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(welcomeText.getBody()).get("value").asText()).isEqualTo("test");

        // application-info
        final ResponseEntity<ApplicationInfoSo> applicationInfo = restTemplate.exchange(
                getURI("/config/application-info"),
                HttpMethod.POST,
                new HttpEntity<>(
                        new ApplicationInfoSo(
                                List.of(
                                        new ApplicationInfoItemSo("testTitleEn", "testTextEn", "testImageEn")
                                )
                        ),
                        headers
                ),
                ApplicationInfoSo.class
        );
        assertThat(applicationInfo.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(applicationInfo.getBody()).isNotNull();
        assertThat(applicationInfo.getBody().items().get(0).title()).isEqualTo("testTitleEn");
        assertThat(applicationInfo.getBody().items().get(0).text()).isEqualTo("testTextEn");
        assertThat(applicationInfo.getBody().items().get(0).imageFileName()).isEqualTo("testImageEn");

        // company-info
        final ResponseEntity<CompanyInfoSo> companyInfo = restTemplate.exchange(
                getURI("/config/company-info"),
                HttpMethod.POST,
                new HttpEntity<>(
                        new CompanyInfoSo(
                                "testNameEn",
                                "testStreetEn",
                                "testCityEn",
                                "000 00",
                                "TestStateEn",
                                "+000 000 000 000",
                                "test@test",
                                "testBusinessId",
                                "testTaxId",
                                "testVatRegNo",
                                "testCommercialRegisterInfoEn",
                                "testMapUrl"
                        ),
                        headers
                ),
                CompanyInfoSo.class
        );
        assertThat(companyInfo.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(companyInfo.getBody()).isNotNull();
        assertThat(companyInfo.getBody().name()).isEqualTo("testNameEn");
        assertThat(companyInfo.getBody().street()).isEqualTo("testStreetEn");
        assertThat(companyInfo.getBody().city()).isEqualTo("testCityEn");
        assertThat(companyInfo.getBody().zipCode()).isEqualTo("000 00");
        assertThat(companyInfo.getBody().state()).isEqualTo("TestStateEn");
        assertThat(companyInfo.getBody().phone()).isEqualTo("+000 000 000 000");
        assertThat(companyInfo.getBody().mail()).isEqualTo("test@test");
        assertThat(companyInfo.getBody().businessId()).isEqualTo("testBusinessId");
        assertThat(companyInfo.getBody().taxId()).isEqualTo("testTaxId");
        assertThat(companyInfo.getBody().vatRegNo()).isEqualTo("testVatRegNo");
        assertThat(companyInfo.getBody().commercialRegisterInfo()).isEqualTo("testCommercialRegisterInfoEn");
        assertThat(companyInfo.getBody().mapUrl()).isEqualTo("testMapUrl");

        // cookies-info
        final ResponseEntity<JsonNode> cookiesInfo = restTemplate.exchange(
                getURI("/config/cookies-info"),
                HttpMethod.POST,
                new HttpEntity<>(
                        new SingleValueBody<>("test"),
                        headers
                ),
                JsonNode.class
        );
        assertThat(cookiesInfo.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(cookiesInfo.getBody()).get("value").asText()).isEqualTo("test");

        // gdpr-info
        final ResponseEntity<JsonNode> gdprInfo = restTemplate.exchange(
                getURI("/config/gdpr-info"),
                HttpMethod.POST,
                new HttpEntity<>(
                        new SingleValueBody<>("test"),
                        headers
                ),
                JsonNode.class
        );
        assertThat(gdprInfo.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(gdprInfo.getBody()).get("value").asText()).isEqualTo("test");

        // working-hours
        final ResponseEntity<JsonNode> workingHours = restTemplate.exchange(
                getURI("/config/working-hours"),
                HttpMethod.POST,
                new HttpEntity<>(
                        new SingleValueBody<>("test"),
                        headers
                ),
                JsonNode.class
        );
        assertThat(workingHours.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(workingHours.getBody()).get("value").asText()).isEqualTo("test");
    }
}
