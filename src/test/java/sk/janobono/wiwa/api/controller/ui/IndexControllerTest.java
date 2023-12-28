package sk.janobono.wiwa.api.controller.ui;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sk.janobono.wiwa.api.controller.BaseControllerTest;
import sk.janobono.wiwa.api.model.ApplicationPropertiesWeb;
import sk.janobono.wiwa.business.model.ui.ApplicationInfoSo;
import sk.janobono.wiwa.business.model.ui.CompanyInfoSo;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;
import sk.janobono.wiwa.model.Unit;
import sk.janobono.wiwa.model.WiwaProperty;

import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class IndexControllerTest extends BaseControllerTest {

    @Autowired
    public ImageUtil imageUtil;

    @Autowired
    public ApplicationPropertyRepository applicationPropertyRepository;

    @Test
    void fullTest() {
        // application-info
        final ApplicationPropertiesWeb applicationProperties = restTemplate.getForObject(getURI("/ui/application-properties"), ApplicationPropertiesWeb.class);
        assertThat(applicationProperties).isNotNull();
        assertThat(applicationProperties.defaultLocale()).isEqualTo("en_US");
        assertThat(applicationProperties.appTitle()).isEqualTo("Wiwa");
        assertThat(applicationProperties.appDescription()).isEqualTo("Woodworking Industry Web Application");
        assertThat(applicationProperties.tokenExpiresIn()).isEqualTo(15);

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
        final ApplicationInfoSo applicationInfo = restTemplate.getForObject(getURI("/ui/application-info"), ApplicationInfoSo.class);
        assertThat(applicationInfo).isNotNull();
        assertThat(applicationInfo.items().size()).isEqualTo(3);
        assertThat(applicationInfo.items().get(0)).isNotBlank();
        assertThat(applicationInfo.items().get(1)).isNotBlank();
        assertThat(applicationInfo.items().get(2)).isNotBlank();

        // company-info
        final CompanyInfoSo companyInfoDto = restTemplate.getForObject(getURI("/ui/company-info"), CompanyInfoSo.class);
        assertThat(companyInfoDto).isNotNull();
        assertThat(companyInfoDto.name()).isEqualTo("WIWA, Ltd.");
        assertThat(companyInfoDto.street()).isEqualTo("Street 12/4567");
        assertThat(companyInfoDto.city()).isEqualTo("Zvolen");
        assertThat(companyInfoDto.zipCode()).isEqualTo("960 01");
        assertThat(companyInfoDto.state()).isEqualTo("Slovakia");
        assertThat(companyInfoDto.phone()).isEqualTo("+421 111 111 111");
        assertThat(companyInfoDto.mail()).isEqualTo("mail@domain.sk");
        assertThat(companyInfoDto.businessId()).isEqualTo("11 111 111");
        assertThat(companyInfoDto.taxId()).isEqualTo("1111111111");
        assertThat(companyInfoDto.vatRegNo()).isEqualTo("SK1111111111");
        assertThat(companyInfoDto.commercialRegisterInfo()).isEqualTo("The company is registered in the Commercial Register of the District Court in Zvolen, section Sro, insert number 11111/P");
        assertThat(companyInfoDto.mapUrl()).isEqualTo("https://maps.google.com/maps?q=Zvolen&t=&z=13&ie=UTF8&iwloc=&output=embed");

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
        final Map units = Objects.requireNonNull(restTemplate.getForObject(getURI("/ui/units"), Map.class));
        assertThat(units).isNotNull();
        for (final Unit unit : Unit.values()) {
            assertThat(units.get(unit.name())).isNotNull();
        }
    }
}
