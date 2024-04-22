package sk.janobono.wiwa.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import sk.janobono.wiwa.api.model.SingleValueBodyWebDto;
import sk.janobono.wiwa.api.model.application.*;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
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

        // Vat rate
        final var newVatRate = setVatRate(headers, BigDecimal.valueOf(25L));
        assertThat(newVatRate).isEqualTo(getVatRate(headers));

        // manufacture-properties
        final ResponseEntity<ManufacturePropertiesWebDto> manufactureProperties = restTemplate.exchange(
                getURI("/config/manufacture-properties"),
                HttpMethod.POST,
                new HttpEntity<>(
                        new ManufacturePropertiesWebDto(
                                new Quantity(new BigDecimal("1000.0000"), Unit.MILLIMETER),
                                new Quantity(new BigDecimal("1000.0000"), Unit.MILLIMETER),
                                new Quantity(new BigDecimal("1000.0000"), Unit.MILLIMETER),
                                new Quantity(new BigDecimal("1000.0000"), Unit.MILLIMETER),
                                new Quantity(new BigDecimal("1000.0000"), Unit.MILLIMETER),
                                new Quantity(new BigDecimal("1000.0000"), Unit.MILLIMETER),
                                new Quantity(new BigDecimal("1000.0000"), Unit.MILLIMETER)
                        ),
                        headers
                ),
                ManufacturePropertiesWebDto.class
        );
        assertThat(manufactureProperties.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(manufactureProperties.getBody()).minimalBoardDimension().quantity()).isEqualTo(new BigDecimal("1000.0000"));

        // price-for-gluing-layer
        ResponseEntity<PriceForGluingLayerWebDto> priceForGluingLayer = restTemplate.exchange(
                getURI("/config/price-for-gluing-layer"),
                HttpMethod.POST,
                new HttpEntity<>(
                        new PriceForGluingLayerWebDto(
                                new Quantity(BigDecimal.ONE, Unit.SQUARE_METER),
                                new Money(new BigDecimal("123.123"), Unit.EUR)
                        ),
                        headers
                ),
                PriceForGluingLayerWebDto.class
        );
        assertThat(priceForGluingLayer.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(priceForGluingLayer.getBody()).price().amount()).isEqualTo(new BigDecimal("123.123"));

        priceForGluingLayer = restTemplate.exchange(
                getURI("/config/price-for-gluing-layer"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                PriceForGluingLayerWebDto.class
        );
        assertThat(priceForGluingLayer.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(priceForGluingLayer.getBody()).price().amount()).isEqualTo(new BigDecimal("123.123"));

        // prices-for-cutting
        ResponseEntity<PriceForCuttingWebDto[]> pricesForCutting = restTemplate.exchange(
                getURI("/config/prices-for-cutting"),
                HttpMethod.POST,
                new HttpEntity<>(
                        List.of(new PriceForCuttingWebDto(
                                new Quantity(BigDecimal.ONE, Unit.MILLIMETER),
                                new Quantity(BigDecimal.ONE, Unit.MILLIMETER),
                                new Money(BigDecimal.ZERO, Unit.EUR)
                        )),
                        headers
                ),
                PriceForCuttingWebDto[].class
        );
        assertThat(pricesForCutting.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(pricesForCutting.getBody())[0].price().amount()).isEqualTo(BigDecimal.ZERO);

        pricesForCutting = restTemplate.exchange(
                getURI("/config/prices-for-cutting"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                PriceForCuttingWebDto[].class
        );
        assertThat(pricesForCutting.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(pricesForCutting.getBody())[0].price().amount()).isEqualTo(BigDecimal.ZERO);

        // prices-for-gluing-edge
        ResponseEntity<PriceForGluingEdgeWebDto[]> pricesForGluingEdge = restTemplate.exchange(
                getURI("/config/prices-for-gluing-edge"),
                HttpMethod.POST,
                new HttpEntity<>(
                        List.of(new PriceForGluingEdgeWebDto(
                                new Quantity(BigDecimal.ONE, Unit.MILLIMETER),
                                new Quantity(BigDecimal.ONE, Unit.MILLIMETER),
                                new Money(BigDecimal.ZERO, Unit.EUR)
                        )),
                        headers
                ),
                PriceForGluingEdgeWebDto[].class
        );
        assertThat(pricesForGluingEdge.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(pricesForGluingEdge.getBody())[0].price().amount()).isEqualTo(BigDecimal.ZERO);

        pricesForGluingEdge = restTemplate.exchange(
                getURI("/config/prices-for-gluing-edge"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                PriceForGluingEdgeWebDto[].class
        );
        assertThat(pricesForGluingEdge.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(pricesForGluingEdge.getBody())[0].price().amount()).isEqualTo(BigDecimal.ZERO);

        // free-days
        final ResponseEntity<FreeDayWebDto[]> freeDays = restTemplate.exchange(
                getURI("/config/free-days"),
                HttpMethod.POST,
                new HttpEntity<>(
                        List.of(new FreeDayWebDto("test", 1, 1)),
                        headers
                ),
                FreeDayWebDto[].class
        );
        assertThat(freeDays.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(freeDays.getBody())[0].name()).isEqualTo("test");

    }

    private BigDecimal getVatRate(final HttpHeaders headers) {
        final ResponseEntity<ObjectNode> response = restTemplate.exchange(
                getURI("/config/vat-rate"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ObjectNode.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody().get("value").decimalValue();
    }

    private BigDecimal setVatRate(final HttpHeaders headers, final BigDecimal vatRate) {
        final ResponseEntity<ObjectNode> response = restTemplate.exchange(
                getURI("/config/vat-rate"),
                HttpMethod.POST,
                new HttpEntity<>(new SingleValueBodyWebDto<>(vatRate), headers),
                ObjectNode.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody().get("value").decimalValue();
    }
}
