package sk.janobono.wiwa.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import sk.janobono.wiwa.BaseIntegrationTest;
import sk.janobono.wiwa.business.model.ApplicationImageSo;
import sk.janobono.wiwa.business.model.ui.*;
import sk.janobono.wiwa.common.component.ImageUtil;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class UiManagementControllerIT extends BaseIntegrationTest {

    @Autowired
    public ImageUtil imageUtil;

    @Autowired
    public ApplicationPropertyRepository applicationPropertyRepository;

    @Test
    void fullTest() throws IOException {
        String token = signIn(DEFAULT_MANAGER, PASSWORD).token();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        // logo
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", new ByteArrayResource(imageUtil.generateMessageImage("test")) {
            @Override
            public String getFilename() {
                return "test.png";
            }
        });
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(form, headers);

        ResponseEntity<ApplicationImageSo> uploadedImage = restTemplate.exchange(
                getURI("/ui-management/logo"),
                HttpMethod.POST,
                httpEntity,
                ApplicationImageSo.class
        );
        assertThat(uploadedImage.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(uploadedImage.getBody()).isNotNull();
        assertThat(uploadedImage.hasBody()).isTrue();
        assertThat(uploadedImage.getBody().fileName()).isEqualTo("logo.png");
        assertThat(uploadedImage.getBody().fileType()).isEqualTo(MediaType.IMAGE_PNG_VALUE);

        // title
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<JsonNode> title = restTemplate.exchange(
                getURI("/ui-management/title"),
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
        LocalizedDataSo<String> titleLocalizedData = getLocalizedProperties(title.getBody());
        assertThat(titleLocalizedData.items().size()).isEqualTo(2);
        assertThat(titleLocalizedData.items().get(0).language()).isEqualTo("en");
        assertThat(titleLocalizedData.items().get(0).data()).isEqualTo("testEn");
        assertThat(titleLocalizedData.items().get(1).language()).isEqualTo("sk");
        assertThat(titleLocalizedData.items().get(1).data()).isEqualTo("testSk");

        // welcome-text
        ResponseEntity<JsonNode> welcomeText = restTemplate.exchange(
                getURI("/ui-management/welcome-text"),
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
        LocalizedDataSo<String> welcomeTextLocalizedData = getLocalizedProperties(welcomeText.getBody());
        assertThat(welcomeTextLocalizedData.items().size()).isEqualTo(2);
        assertThat(welcomeTextLocalizedData.items().get(0).language()).isEqualTo("en");
        assertThat(welcomeTextLocalizedData.items().get(0).data()).isEqualTo("testEn");
        assertThat(welcomeTextLocalizedData.items().get(1).language()).isEqualTo("sk");
        assertThat(welcomeTextLocalizedData.items().get(1).data()).isEqualTo("testSk");

        // application-info
        ResponseEntity<JsonNode> applicationInfo = restTemplate.exchange(
                getURI("/ui-management/application-info"),
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
        LocalizedDataSo<ApplicationInfoSo> applicationInfoLocalizedData = getLocalizedApplicationInfo(applicationInfo.getBody());
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
        ResponseEntity<JsonNode> companyInfo = restTemplate.exchange(
                getURI("/ui-management/company-info"),
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
        LocalizedDataSo<CompanyInfoSo> companyInfoLocalizedData = getCompanyInfo(companyInfo.getBody());
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
        ResponseEntity<JsonNode> cookiesInfo = restTemplate.exchange(
                getURI("/ui-management/cookies-info"),
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
        LocalizedDataSo<String> cookiesInfoLocalizedData = getLocalizedProperties(cookiesInfo.getBody());
        assertThat(cookiesInfoLocalizedData.items().size()).isEqualTo(2);
        assertThat(cookiesInfoLocalizedData.items().get(0).language()).isEqualTo("en");
        assertThat(cookiesInfoLocalizedData.items().get(0).data()).isEqualTo("testEn");
        assertThat(cookiesInfoLocalizedData.items().get(1).language()).isEqualTo("sk");
        assertThat(cookiesInfoLocalizedData.items().get(1).data()).isEqualTo("testSk");

        // gdpr-info
        ResponseEntity<JsonNode> gdprInfo = restTemplate.exchange(
                getURI("/ui-management/gdpr-info"),
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
        LocalizedDataSo<String> gdprInfoLocalizedData = getLocalizedProperties(gdprInfo.getBody());
        assertThat(gdprInfoLocalizedData.items().size()).isEqualTo(2);
        assertThat(gdprInfoLocalizedData.items().get(0).language()).isEqualTo("en");
        assertThat(gdprInfoLocalizedData.items().get(0).data()).isEqualTo("testEn");
        assertThat(gdprInfoLocalizedData.items().get(1).language()).isEqualTo("sk");
        assertThat(gdprInfoLocalizedData.items().get(1).data()).isEqualTo("testSk");

        // working-hours
        ResponseEntity<JsonNode> workingHours = restTemplate.exchange(
                getURI("/ui-management/working-hours"),
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
        LocalizedDataSo<String> workingHoursLocalizedData = getLocalizedProperties(workingHours.getBody());
        assertThat(workingHoursLocalizedData.items().size()).isEqualTo(2);
        assertThat(workingHoursLocalizedData.items().get(0).language()).isEqualTo("en");
        assertThat(workingHoursLocalizedData.items().get(0).data()).isEqualTo("testEn");
        assertThat(workingHoursLocalizedData.items().get(1).language()).isEqualTo("sk");
        assertThat(workingHoursLocalizedData.items().get(1).data()).isEqualTo("testSk");
    }

    private LocalizedDataSo<String> getLocalizedProperties(JsonNode jsonNode) {
        LocalizedDataSo<String> result = new LocalizedDataSo<>(new ArrayList<>());
        if (Objects.nonNull(jsonNode)) {
            JsonNode items = jsonNode.get("items");
            for (JsonNode item : items) {
                String language = item.get("language").asText();
                String data = item.get("data").asText();
                result.items().add(new LocalizedDataItemSo<>(language, data));
            }
        }
        return result;
    }

    private LocalizedDataSo<ApplicationInfoSo> getLocalizedApplicationInfo(JsonNode jsonNode) throws IOException {
        LocalizedDataSo<ApplicationInfoSo> result = new LocalizedDataSo<>(new ArrayList<>());
        if (Objects.nonNull(jsonNode)) {
            JsonNode localizedItems = jsonNode.get("items");
            for (JsonNode localizedItem : localizedItems) {
                String language = localizedItem.get("language").asText();
                List<ApplicationInfoItemSo> items = new ArrayList<>();
                JsonNode localizedData = localizedItem.get("data");
                for (JsonNode item : localizedData.get("items")) {
                    items.add(objectMapper.readValue(item.traverse(), ApplicationInfoItemSo.class));
                }
                result.items().add(new LocalizedDataItemSo<>(language, new ApplicationInfoSo(items)));
            }
        }
        return result;
    }

    private LocalizedDataSo<CompanyInfoSo> getCompanyInfo(JsonNode jsonNode) throws IOException {
        LocalizedDataSo<CompanyInfoSo> result = new LocalizedDataSo<>(new ArrayList<>());
        if (Objects.nonNull(jsonNode)) {
            JsonNode items = jsonNode.get("items");
            for (JsonNode item : items) {
                String language = item.get("language").asText();
                CompanyInfoSo companyInfoDto = objectMapper.readValue(item.get("data").traverse(), CompanyInfoSo.class);
                result.items().add(new LocalizedDataItemSo<>(language, companyInfoDto));
            }
        }
        return result;
    }
}
