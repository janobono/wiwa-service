package sk.janobono.wiwa.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import sk.janobono.wiwa.api.model.ApplicationPropertiesWebDto;
import sk.janobono.wiwa.api.model.CaptchaWebDto;
import sk.janobono.wiwa.api.model.CompanyInfoWebDto;
import sk.janobono.wiwa.api.model.UnitWebDto;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;
import sk.janobono.wiwa.model.WiwaProperty;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class UiControllerTest extends BaseControllerTest {

    @Autowired
    public ImageUtil imageUtil;

    @Autowired
    public ApplicationPropertyRepository applicationPropertyRepository;

    @Test
    void fullTest() {
        // application-info
        final ApplicationPropertiesWebDto applicationProperties = restTemplate.getForObject(getURI("/ui/application-properties"), ApplicationPropertiesWebDto.class);
        assertThat(applicationProperties).isNotNull();
        assertThat(applicationProperties.defaultLocale()).isEqualTo("en_US");
        assertThat(applicationProperties.appTitle()).isEqualTo("Wiwa");
        assertThat(applicationProperties.appDescription()).isEqualTo("Woodworking Industry Web Application");
        assertThat(applicationProperties.tokenExpiresIn()).isEqualTo(15);

        // captcha
        final ResponseEntity<CaptchaWebDto> response = restTemplate.exchange(
                getURI("/ui/captcha"), HttpMethod.GET, HttpEntity.EMPTY, CaptchaWebDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        // logo
        final byte[] logo = restTemplate.getForObject(getURI("/ui/logo"), byte[].class);
        assertThat(logo).isEqualTo(imageUtil.generateMessageImage(null));

        // title
        final String title = Objects.requireNonNull(restTemplate.getForObject(getURI("/ui/title"), JsonNode.class)).get("value").textValue();
        assertThat(title).isEqualTo("WIWA - Internet store");

        // welcome-text
        final String welcomeText = Objects.requireNonNull(restTemplate.getForObject(getURI("/ui/welcome-text"), JsonNode.class)).get("value").textValue();
        assertThat(welcomeText).isNotBlank();

        // application-info
        final String[] applicationInfo = restTemplate.getForObject(getURI("/ui/application-info"), String[].class);
        assertThat(applicationInfo).isNotNull();
        assertThat(applicationInfo.length).isEqualTo(3);

        // company-info
        final CompanyInfoWebDto companyInfo = restTemplate.getForObject(getURI("/ui/company-info"), CompanyInfoWebDto.class);
        assertThat(companyInfo).isNotNull();
        assertThat(companyInfo.name()).isEqualTo("WIWA, Ltd.");
        assertThat(companyInfo.street()).isEqualTo("Street 12/4567");
        assertThat(companyInfo.city()).isEqualTo("Zvolen");
        assertThat(companyInfo.zipCode()).isEqualTo("960 01");
        assertThat(companyInfo.state()).isEqualTo("Slovakia");
        assertThat(companyInfo.phone()).isEqualTo("+421 111 111 111");
        assertThat(companyInfo.mail()).isEqualTo("mail@domain.sk");
        assertThat(companyInfo.businessId()).isEqualTo("11 111 111");
        assertThat(companyInfo.taxId()).isEqualTo("1111111111");
        assertThat(companyInfo.vatRegNo()).isEqualTo("SK1111111111");
        assertThat(companyInfo.commercialRegisterInfo()).isEqualTo("The company is registered in the Commercial Register of the District Court in Zvolen, section Sro, insert number 11111/P");
        assertThat(companyInfo.mapUrl()).isEqualTo("https://maps.google.com/maps?q=Zvolen&t=&z=13&ie=UTF8&iwloc=&output=embed");

        // business-conditions
        final String businessConditions = Objects.requireNonNull(restTemplate.getForObject(getURI("/ui/business-conditions"), JsonNode.class)).get("value").textValue();
        assertThat(businessConditions).isEqualTo(
                applicationPropertyRepository.findByGroupAndKey(
                        WiwaProperty.APP_BUSINESS_CONDITIONS.getGroup(),
                        WiwaProperty.APP_BUSINESS_CONDITIONS.getKey()
                ).orElseThrow().getValue()
        );

        // cookies-info
        final String cookiesInfo = Objects.requireNonNull(restTemplate.getForObject(getURI("/ui/cookies-info"), JsonNode.class)).get("value").textValue();
        assertThat(cookiesInfo).isEqualTo(
                applicationPropertyRepository.findByGroupAndKey(
                        WiwaProperty.APP_COOKIES_INFO.getGroup(),
                        WiwaProperty.APP_COOKIES_INFO.getKey()
                ).orElseThrow().getValue()
        );

        // gdpr-info
        final String gdprInfo = Objects.requireNonNull(restTemplate.getForObject(getURI("/ui/gdpr-info"), JsonNode.class)).get("value").textValue();
        assertThat(gdprInfo).isEqualTo(
                applicationPropertyRepository.findByGroupAndKey(
                        WiwaProperty.APP_GDPR_INFO.getGroup(),
                        WiwaProperty.APP_GDPR_INFO.getKey()
                ).orElseThrow().getValue()
        );

        // working-hours
        final String workingHours = Objects.requireNonNull(restTemplate.getForObject(getURI("/ui/working-hours"), JsonNode.class)).get("value").textValue();
        assertThat(workingHours).isEqualTo(
                applicationPropertyRepository.findByGroupAndKey(
                        WiwaProperty.APP_WORKING_HOURS.getGroup(),
                        WiwaProperty.APP_WORKING_HOURS.getKey()
                ).orElseThrow().getValue()
        );

        // units
        final UnitWebDto[] units = Objects.requireNonNull(restTemplate.getForObject(getURI("/ui/units"), UnitWebDto[].class));
        assertThat(units).isNotNull();
    }
}
