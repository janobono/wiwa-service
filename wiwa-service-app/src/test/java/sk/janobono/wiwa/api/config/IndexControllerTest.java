package sk.janobono.wiwa.api.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sk.janobono.wiwa.BaseIntegrationTest;
import sk.janobono.wiwa.api.model.ApplicationImageWeb;
import sk.janobono.wiwa.business.model.ui.*;
import sk.janobono.wiwa.common.component.ImageUtil;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class IndexControllerTest extends BaseIntegrationTest {

    @Autowired
    public ImageUtil imageUtil;

    @Autowired
    public ApplicationPropertyRepository applicationPropertyRepository;

    @Test
    void fullTest() throws IOException {
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
                        new LocalizedDataSo<>(
                                List.of(
                                        new LocalizedDataItemSo<>("en", "testEn"),
                                        new LocalizedDataItemSo<>("sk", "testSk")
                                )
                        ),
                        headers
                ),
                JsonNode.class
        );
        assertThat(title.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(title.getBody()).isNotNull();
        final LocalizedDataSo<String> titleLocalizedData = getLocalizedProperties(title.getBody());
        assertThat(titleLocalizedData.items().size()).isEqualTo(2);
        assertThat(titleLocalizedData.items().get(0).language()).isEqualTo("en");
        assertThat(titleLocalizedData.items().get(0).data()).isEqualTo("testEn");
        assertThat(titleLocalizedData.items().get(1).language()).isEqualTo("sk");
        assertThat(titleLocalizedData.items().get(1).data()).isEqualTo("testSk");

        // welcome-text
        final ResponseEntity<JsonNode> welcomeText = restTemplate.exchange(
                getURI("/config/welcome-text"),
                HttpMethod.POST,
                new HttpEntity<>(
                        new LocalizedDataSo<>(
                                List.of(
                                        new LocalizedDataItemSo<>("en", "testEn"),
                                        new LocalizedDataItemSo<>("sk", "testSk")
                                )
                        ),
                        headers
                ),
                JsonNode.class
        );
        assertThat(welcomeText.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(welcomeText.getBody()).isNotNull();
        final LocalizedDataSo<String> welcomeTextLocalizedData = getLocalizedProperties(welcomeText.getBody());
        assertThat(welcomeTextLocalizedData.items().size()).isEqualTo(2);
        assertThat(welcomeTextLocalizedData.items().get(0).language()).isEqualTo("en");
        assertThat(welcomeTextLocalizedData.items().get(0).data()).isEqualTo("testEn");
        assertThat(welcomeTextLocalizedData.items().get(1).language()).isEqualTo("sk");
        assertThat(welcomeTextLocalizedData.items().get(1).data()).isEqualTo("testSk");

        // application-info
        final ResponseEntity<JsonNode> applicationInfo = restTemplate.exchange(
                getURI("/config/application-info"),
                HttpMethod.POST,
                new HttpEntity<>(
                        new LocalizedDataSo<>(
                                List.of(
                                        new LocalizedDataItemSo<>("en",
                                                new ApplicationInfoSo(
                                                        List.of(
                                                                new ApplicationInfoItemSo("testTitleEn", "testTextEn", "testImageEn")
                                                        )
                                                )
                                        ),
                                        new LocalizedDataItemSo<>("sk",
                                                new ApplicationInfoSo(
                                                        List.of(
                                                                new ApplicationInfoItemSo("testTitleSk", "testTextSk", "testImageSk")
                                                        )
                                                )
                                        )
                                )
                        ),
                        headers
                ),
                JsonNode.class
        );
        assertThat(applicationInfo.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(applicationInfo.getBody()).isNotNull();
        final LocalizedDataSo<ApplicationInfoSo> applicationInfoLocalizedData = getLocalizedApplicationInfo(applicationInfo.getBody());
        assertThat(applicationInfoLocalizedData.items().size()).isEqualTo(2);
        assertThat(applicationInfoLocalizedData.items().get(0).language()).isEqualTo("en");
        assertThat(applicationInfoLocalizedData.items().get(0).data().items().size()).isEqualTo(1);
        assertThat(applicationInfoLocalizedData.items().get(0).data().items().get(0).title()).isEqualTo("testTitleEn");
        assertThat(applicationInfoLocalizedData.items().get(0).data().items().get(0).text()).isEqualTo("testTextEn");
        assertThat(applicationInfoLocalizedData.items().get(0).data().items().get(0).imageFileName()).isEqualTo("testImageEn");
        assertThat(applicationInfoLocalizedData.items().get(1).language()).isEqualTo("sk");
        assertThat(applicationInfoLocalizedData.items().get(1).data().items().size()).isEqualTo(1);
        assertThat(applicationInfoLocalizedData.items().get(1).data().items().get(0).title()).isEqualTo("testTitleSk");
        assertThat(applicationInfoLocalizedData.items().get(1).data().items().get(0).text()).isEqualTo("testTextSk");
        assertThat(applicationInfoLocalizedData.items().get(1).data().items().get(0).imageFileName()).isEqualTo("testImageSk");

        // company-info
        final ResponseEntity<JsonNode> companyInfo = restTemplate.exchange(
                getURI("/config/company-info"),
                HttpMethod.POST,
                new HttpEntity<>(
                        new LocalizedDataSo<>(
                                List.of(
                                        new LocalizedDataItemSo<>("en", new CompanyInfoSo(
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
                                        )),
                                        new LocalizedDataItemSo<>("sk", new CompanyInfoSo(
                                                "testNameSk",
                                                "testStreetSk",
                                                "testCitySk",
                                                "000 00",
                                                "TestStateSk",
                                                "+000 000 000 000",
                                                "test@test",
                                                "testBusinessId",
                                                "testTaxId",
                                                "testVatRegNo",
                                                "testCommercialRegisterInfoSk",
                                                "testMapUrl"
                                        ))
                                )
                        ),
                        headers
                ),
                JsonNode.class
        );
        assertThat(companyInfo.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(companyInfo.getBody()).isNotNull();
        final LocalizedDataSo<CompanyInfoSo> companyInfoLocalizedData = getCompanyInfo(companyInfo.getBody());
        assertThat(companyInfoLocalizedData.items().size()).isEqualTo(2);
        assertThat(companyInfoLocalizedData.items().get(0).language()).isEqualTo("en");
        assertThat(companyInfoLocalizedData.items().get(0).data().name()).isEqualTo("testNameEn");
        assertThat(companyInfoLocalizedData.items().get(0).data().street()).isEqualTo("testStreetEn");
        assertThat(companyInfoLocalizedData.items().get(0).data().city()).isEqualTo("testCityEn");
        assertThat(companyInfoLocalizedData.items().get(0).data().state()).isEqualTo("TestStateEn");
        assertThat(companyInfoLocalizedData.items().get(1).language()).isEqualTo("sk");
        assertThat(companyInfoLocalizedData.items().get(1).data().name()).isEqualTo("testNameSk");
        assertThat(companyInfoLocalizedData.items().get(1).data().street()).isEqualTo("testStreetSk");
        assertThat(companyInfoLocalizedData.items().get(1).data().city()).isEqualTo("testCitySk");
        assertThat(companyInfoLocalizedData.items().get(1).data().state()).isEqualTo("TestStateSk");

        // cookies-info
        final ResponseEntity<JsonNode> cookiesInfo = restTemplate.exchange(
                getURI("/config/cookies-info"),
                HttpMethod.POST,
                new HttpEntity<>(
                        new LocalizedDataSo<>(
                                List.of(
                                        new LocalizedDataItemSo<>("en", "testEn"),
                                        new LocalizedDataItemSo<>("sk", "testSk")
                                )
                        ),
                        headers
                ),
                JsonNode.class
        );
        assertThat(cookiesInfo.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cookiesInfo.getBody()).isNotNull();
        final LocalizedDataSo<String> cookiesInfoLocalizedData = getLocalizedProperties(cookiesInfo.getBody());
        assertThat(cookiesInfoLocalizedData.items().size()).isEqualTo(2);
        assertThat(cookiesInfoLocalizedData.items().get(0).language()).isEqualTo("en");
        assertThat(cookiesInfoLocalizedData.items().get(0).data()).isEqualTo("testEn");
        assertThat(cookiesInfoLocalizedData.items().get(1).language()).isEqualTo("sk");
        assertThat(cookiesInfoLocalizedData.items().get(1).data()).isEqualTo("testSk");

        // gdpr-info
        final ResponseEntity<JsonNode> gdprInfo = restTemplate.exchange(
                getURI("/config/gdpr-info"),
                HttpMethod.POST,
                new HttpEntity<>(
                        new LocalizedDataSo<>(
                                List.of(
                                        new LocalizedDataItemSo<>("en", "testEn"),
                                        new LocalizedDataItemSo<>("sk", "testSk")
                                )
                        ),
                        headers
                ),
                JsonNode.class
        );
        assertThat(gdprInfo.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(gdprInfo.getBody()).isNotNull();
        final LocalizedDataSo<String> gdprInfoLocalizedData = getLocalizedProperties(gdprInfo.getBody());
        assertThat(gdprInfoLocalizedData.items().size()).isEqualTo(2);
        assertThat(gdprInfoLocalizedData.items().get(0).language()).isEqualTo("en");
        assertThat(gdprInfoLocalizedData.items().get(0).data()).isEqualTo("testEn");
        assertThat(gdprInfoLocalizedData.items().get(1).language()).isEqualTo("sk");
        assertThat(gdprInfoLocalizedData.items().get(1).data()).isEqualTo("testSk");

        // working-hours
        final ResponseEntity<JsonNode> workingHours = restTemplate.exchange(
                getURI("/config/working-hours"),
                HttpMethod.POST,
                new HttpEntity<>(
                        new LocalizedDataSo<>(
                                List.of(
                                        new LocalizedDataItemSo<>("en", "testEn"),
                                        new LocalizedDataItemSo<>("sk", "testSk")
                                )
                        ),
                        headers
                ),
                JsonNode.class
        );
        assertThat(workingHours.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(workingHours.getBody()).isNotNull();
        final LocalizedDataSo<String> workingHoursLocalizedData = getLocalizedProperties(workingHours.getBody());
        assertThat(workingHoursLocalizedData.items().size()).isEqualTo(2);
        assertThat(workingHoursLocalizedData.items().get(0).language()).isEqualTo("en");
        assertThat(workingHoursLocalizedData.items().get(0).data()).isEqualTo("testEn");
        assertThat(workingHoursLocalizedData.items().get(1).language()).isEqualTo("sk");
        assertThat(workingHoursLocalizedData.items().get(1).data()).isEqualTo("testSk");
    }

    private LocalizedDataSo<String> getLocalizedProperties(final JsonNode jsonNode) {
        final LocalizedDataSo<String> result = new LocalizedDataSo<>(new ArrayList<>());
        if (Objects.nonNull(jsonNode)) {
            final JsonNode items = jsonNode.get("items");
            for (final JsonNode item : items) {
                final String language = item.get("language").asText();
                final String data = item.get("data").asText();
                result.items().add(new LocalizedDataItemSo<>(language, data));
            }
        }
        return result;
    }

    private LocalizedDataSo<ApplicationInfoSo> getLocalizedApplicationInfo(final JsonNode jsonNode) throws IOException {
        final LocalizedDataSo<ApplicationInfoSo> result = new LocalizedDataSo<>(new ArrayList<>());
        if (Objects.nonNull(jsonNode)) {
            final JsonNode localizedItems = jsonNode.get("items");
            for (final JsonNode localizedItem : localizedItems) {
                final String language = localizedItem.get("language").asText();
                final List<ApplicationInfoItemSo> items = new ArrayList<>();
                final JsonNode localizedData = localizedItem.get("data");
                for (final JsonNode item : localizedData.get("items")) {
                    items.add(objectMapper.readValue(item.traverse(), ApplicationInfoItemSo.class));
                }
                result.items().add(new LocalizedDataItemSo<>(language, new ApplicationInfoSo(items)));
            }
        }
        return result;
    }

    private LocalizedDataSo<CompanyInfoSo> getCompanyInfo(final JsonNode jsonNode) throws IOException {
        final LocalizedDataSo<CompanyInfoSo> result = new LocalizedDataSo<>(new ArrayList<>());
        if (Objects.nonNull(jsonNode)) {
            final JsonNode items = jsonNode.get("items");
            for (final JsonNode item : items) {
                final String language = item.get("language").asText();
                final CompanyInfoSo companyInfoDto = objectMapper.readValue(item.get("data").traverse(), CompanyInfoSo.class);
                result.items().add(new LocalizedDataItemSo<>(language, companyInfoDto));
            }
        }
        return result;
    }
}
