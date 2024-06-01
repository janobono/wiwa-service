package sk.janobono.wiwa.api.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import sk.janobono.wiwa.BaseTest;
import sk.janobono.wiwa.api.model.SingleValueBodyWebDto;
import sk.janobono.wiwa.api.model.application.*;
import sk.janobono.wiwa.api.model.captcha.CaptchaWebDto;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UiControllerTest extends BaseTest {

    @Test
    void captchaTest() {
        final ResponseEntity<CaptchaWebDto> responseEntity = restClient.get()
                .uri(getURI("/ui/captcha"))
                .retrieve()
                .toEntity(CaptchaWebDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().captchaImage()).isNotBlank();
        assertThat(responseEntity.getBody().captchaToken()).isNotBlank();
    }

    @Test
    void logoTest() {
        final ResponseEntity<byte[]> responseEntity = restClient.get()
                .uri(getURI("/ui/logo"))
                .retrieve()
                .toEntity(byte[].class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
    }

    @Test
    void applicationImagesTest() {
        final ResponseEntity<byte[]> responseEntity = restClient.get()
                .uri(getURI("/ui/application-images/{fileName}", Map.of("fileName", "image.jpg")))
                .retrieve()
                .toEntity(byte[].class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
    }

    @Test
    void boardImagesTest() {
        final ResponseEntity<byte[]> responseEntity = restClient.get()
                .uri(getURI("/ui/board-images/{id}", Map.of("id", "1")))
                .retrieve()
                .toEntity(byte[].class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
    }

    @Test
    void edgeImagesTest() {
        final ResponseEntity<byte[]> responseEntity = restClient.get()
                .uri(getURI("/ui/edge-images/{id}", Map.of("id", "1")))
                .retrieve()
                .toEntity(byte[].class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
    }

    @Test
    void maintenanceTest() {
        final ResponseEntity<SingleValueBodyWebDto> responseEntity = restClient.get()
                .uri(getURI("/ui/maintenance"))
                .retrieve()
                .toEntity(SingleValueBodyWebDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().value()).isEqualTo(true);
    }

    @Test
    void applicationPropertiesTest() {
        final ResponseEntity<ApplicationPropertiesWebDto> responseEntity = restClient.get()
                .uri(getURI("/ui/application-properties"))
                .retrieve()
                .toEntity(ApplicationPropertiesWebDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().defaultLocale()).isNotBlank();
        assertThat(responseEntity.getBody().appTitle()).isNotBlank();
        assertThat(responseEntity.getBody().appDescription()).isNotBlank();
        assertThat(responseEntity.getBody().tokenExpiresIn()).isNotNull();
        assertThat(responseEntity.getBody().currency()).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"title", "welcome-text", "business-conditions", "cookies-info", "gdpr-info", "order-info", "working-hours"})
    void stringTest(final String path) {
        final ResponseEntity<SingleValueBodyWebDto> responseEntity = restClient.get()
                .uri(getURI("/ui/" + path))
                .retrieve()
                .toEntity(SingleValueBodyWebDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().value().getClass()).isEqualTo(String.class);
    }

    @Test
    void applicationInfoTest() {
        final ResponseEntity<String[]> responseEntity = restClient.get()
                .uri(getURI("/ui/application-info"))
                .retrieve()
                .toEntity(String[].class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody()).isEmpty();
    }

    @Test
    void companyInfoTest() {
        final ResponseEntity<CompanyInfoWebDto> responseEntity = restClient.get()
                .uri(getURI("/ui/company-info"))
                .retrieve()
                .toEntity(CompanyInfoWebDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().name()).isNotBlank();
        assertThat(responseEntity.getBody().street()).isNotBlank();
        assertThat(responseEntity.getBody().city()).isNotBlank();
        assertThat(responseEntity.getBody().zipCode()).isNotBlank();
        assertThat(responseEntity.getBody().state()).isNotBlank();
        assertThat(responseEntity.getBody().phone()).isNotBlank();
        assertThat(responseEntity.getBody().mail()).isNotBlank();
        assertThat(responseEntity.getBody().businessId()).isNotBlank();
        assertThat(responseEntity.getBody().taxId()).isNotBlank();
        assertThat(responseEntity.getBody().vatRegNo()).isNotBlank();
        assertThat(responseEntity.getBody().commercialRegisterInfo()).isNotBlank();
        assertThat(responseEntity.getBody().mapUrl()).isNotBlank();
    }

    @Test
    void unitsTest() {
        final ResponseEntity<UnitWebDto[]> responseEntity = restClient.get()
                .uri(getURI("/ui/units"))
                .retrieve()
                .toEntity(UnitWebDto[].class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody()).hasSize(5);
    }

    @Test
    void freeDaysTest() {
        final ResponseEntity<FreeDayWebDto[]> responseEntity = restClient.get()
                .uri(getURI("/ui/free-days"))
                .retrieve()
                .toEntity(FreeDayWebDto[].class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody()).hasSize(15);
    }

    @Test
    void orderPropertiesTest() {
        final ResponseEntity<OrderPropertiesWebDto> responseEntity = restClient.get()
                .uri(getURI("/ui/order-properties"))
                .retrieve()
                .toEntity(OrderPropertiesWebDto.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().dimensions()).isEmpty();
        assertThat(responseEntity.getBody().boards()).isEmpty();
        assertThat(responseEntity.getBody().edges()).isEmpty();
        assertThat(responseEntity.getBody().corners()).isEmpty();
        assertThat(responseEntity.getBody().pattern()).isNotEmpty();
        assertThat(responseEntity.getBody().content()).isNotEmpty();
        assertThat(responseEntity.getBody().packageType()).isEmpty();
        assertThat(responseEntity.getBody().csvSeparator()).isEqualTo(";");
        assertThat(responseEntity.getBody().csvReplacements()).isNotEmpty();
        assertThat(responseEntity.getBody().csvColumns()).isEmpty();
    }
}
