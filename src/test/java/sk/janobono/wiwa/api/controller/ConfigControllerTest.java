package sk.janobono.wiwa.api.controller;

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
import sk.janobono.wiwa.api.model.ApplicationImageInfoWebDto;
import sk.janobono.wiwa.api.model.CompanyInfoWebDto;
import sk.janobono.wiwa.api.model.SingleValueBodyWebDto;
import sk.janobono.wiwa.api.model.UnitWebDto;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;
import sk.janobono.wiwa.model.Unit;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigControllerTest extends BaseControllerTest {

    @Autowired
    public ImageUtil imageUtil;

    @Autowired
    public ApplicationPropertyRepository applicationPropertyRepository;

    @Test
    void appImageTest() {
        final String token = signIn(DEFAULT_MANAGER, PASSWORD).token();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(token);

        final MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", new ByteArrayResource(imageUtil.generateMessageImage(null)) {
            @Override
            public String getFilename() {
                return "test.png";
            }
        });
        final HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(form, headers);

        final ResponseEntity<ApplicationImageInfoWebDto> uploadedImage = restTemplate.exchange(
                getURI("/config/application-images"),
                HttpMethod.POST,
                httpEntity,
                ApplicationImageInfoWebDto.class
        );
        assertThat(uploadedImage.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(uploadedImage.getBody()).isNotNull();
        assertThat(uploadedImage.hasBody()).isTrue();
        assertThat(uploadedImage.getBody().fileName()).isEqualTo("test.png");
        assertThat(uploadedImage.getBody().thumbnail().startsWith("data:" + MediaType.IMAGE_PNG_VALUE)).isTrue();

        final byte[] data = restTemplate.getForObject(
                getURI("/ui/application-images/{fileName}", Collections.singletonMap("fileName", "test2.png")),
                byte[].class
        );
        assertThat(data).isNotNull();
        assertThat(data).isEqualTo(imageUtil.generateMessageImage(null));

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        final Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "fileName", "fileType");
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        addPageableToParams(params, pageable);
        final ResponseEntity<JsonNode> response = restTemplate.exchange(
                getURI("/config/application-images", params),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                JsonNode.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        final Page<ApplicationImageInfoWebDto> page = getPage(response.getBody(), pageable, ApplicationImageInfoWebDto.class);
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().getFirst().fileName()).isEqualTo("test.png");
        assertThat(page.getContent().getFirst().thumbnail().startsWith("data:" + MediaType.IMAGE_PNG_VALUE)).isTrue();
    }

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

        final ResponseEntity<ApplicationImageInfoWebDto> uploadedImage = restTemplate.exchange(
                getURI("/config/logo"),
                HttpMethod.POST,
                httpEntity,
                ApplicationImageInfoWebDto.class
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
                        new SingleValueBodyWebDto<>("test"),
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
                        new SingleValueBodyWebDto<>("test"),
                        headers
                ),
                JsonNode.class
        );
        assertThat(welcomeText.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(welcomeText.getBody()).get("value").asText()).isEqualTo("test");

        // application-info
        final ResponseEntity<String[]> applicationInfo = restTemplate.exchange(
                getURI("/config/application-info"),
                HttpMethod.POST,
                new HttpEntity<>(
                        List.of("testTextEn"),
                        headers
                ),
                String[].class
        );
        assertThat(applicationInfo.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(applicationInfo.getBody()).isNotNull();
        assertThat(applicationInfo.getBody()[0]).isEqualTo("testTextEn");

        // company-info
        final ResponseEntity<CompanyInfoWebDto> companyInfo = restTemplate.exchange(
                getURI("/config/company-info"),
                HttpMethod.POST,
                new HttpEntity<>(
                        new CompanyInfoWebDto(
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
                CompanyInfoWebDto.class
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

        // business-conditions
        final ResponseEntity<JsonNode> businessConditions = restTemplate.exchange(
                getURI("/config/business-conditions"),
                HttpMethod.POST,
                new HttpEntity<>(
                        new SingleValueBodyWebDto<>("test"),
                        headers
                ),
                JsonNode.class
        );
        assertThat(businessConditions.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(businessConditions.getBody()).get("value").asText()).isEqualTo("test");

        // cookies-info
        final ResponseEntity<JsonNode> cookiesInfo = restTemplate.exchange(
                getURI("/config/cookies-info"),
                HttpMethod.POST,
                new HttpEntity<>(
                        new SingleValueBodyWebDto<>("test"),
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
                        new SingleValueBodyWebDto<>("test"),
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
                        new SingleValueBodyWebDto<>("test"),
                        headers
                ),
                JsonNode.class
        );
        assertThat(workingHours.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(workingHours.getBody()).get("value").asText()).isEqualTo("test");

        // units
        final ResponseEntity<UnitWebDto[]> units = restTemplate.exchange(
                getURI("/config/units"),
                HttpMethod.POST,
                new HttpEntity<>(
                        List.of(new UnitWebDto(Unit.EUR, "test")),
                        headers
                ),
                UnitWebDto[].class
        );
        assertThat(units.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(units.getBody())[0].id()).isEqualTo(Unit.EUR);
    }
}
