package sk.janobono.wiwa.api.ui;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sk.janobono.wiwa.BaseIntegrationTest;
import sk.janobono.wiwa.business.model.ui.ApplicationInfoSo;
import sk.janobono.wiwa.business.model.ui.CompanyInfoSo;
import sk.janobono.wiwa.common.component.ImageUtil;
import sk.janobono.wiwa.common.model.WiwaProperty;
import sk.janobono.wiwa.dal.repository.ApplicationPropertyRepository;

import static org.assertj.core.api.Assertions.assertThat;

class IndexControllerTest extends BaseIntegrationTest {

    @Autowired
    public ImageUtil imageUtil;

    @Autowired
    public ApplicationPropertyRepository applicationPropertyRepository;

    @Test
    void fullTest() {
        // logo
        final byte[] logo = restTemplate.getForObject(getURI("/ui/logo"), byte[].class);
        assertThat(logo).isEqualTo(imageUtil.generateMessageImage(null));

        // title
        final String titleEn = restTemplate.getForObject(getURI("/ui/title", enQueryParams()), String.class);
        assertThat(titleEn).isEqualTo("WIWA - Internet store");

        final String titleSk = restTemplate.getForObject(getURI("/ui/title", skQueryParams()), String.class);
        assertThat(titleSk).isEqualTo("WIWA - Internetový obchod");

        // welcome-text
        final String welcomeTextEn = restTemplate.getForObject(getURI("/ui/welcome-text", enQueryParams()), String.class);
        assertThat(welcomeTextEn).isEqualTo("If you plan to make the furniture yourself, we offer you the opportunity to order from us quality prepared furniture parts for your kitchen, office or other interior project. We will provide you with the required materials (DTD, MDF, HDF), cutting and gluing the edges exactly according to your wishes.");

        final String welcomeTextSk = restTemplate.getForObject(getURI("/ui/welcome-text", skQueryParams()), String.class);
        assertThat(welcomeTextSk).isEqualTo("V prípade ak si plánujete vyrobiť nábytok samostatne, ponúkame Vám možnosť objednať si u nás kvalitne pripravené nábytkové dielce pre váš projekt kuchyne, kancelárie alebo iného interiéru. Zabezpečíme pre Vás požadované materiály (DTD, MDF, HDF), porez a lepenie hrany presne podľa vaších predstáv.");

        // application-info
        final ApplicationInfoSo applicationInfoEn = restTemplate.getForObject(getURI("/ui/application-info", enQueryParams()), ApplicationInfoSo.class);
        assertThat(applicationInfoEn).isNotNull();
        assertThat(applicationInfoEn.items().size()).isEqualTo(3);
        assertThat(applicationInfoEn.items().get(0).title()).isEqualTo("Cutting and edging");
        assertThat(applicationInfoEn.items().get(0).text()).isEqualTo("Cutting and edging chipboard precisely to measure, laminated chipboards and ABS edges in more than 150 decors.");
        assertThat(applicationInfoEn.items().get(0).imageFileName()).isEqualTo("cutting-and-edging.png");

        final ApplicationInfoSo applicationInfoSk = restTemplate.getForObject(getURI("/ui/application-info", skQueryParams()), ApplicationInfoSo.class);
        assertThat(applicationInfoSk).isNotNull();
        assertThat(applicationInfoSk.items().size()).isEqualTo(3);
        assertThat(applicationInfoSk.items().get(0).title()).isEqualTo("Rezanie a hranovanie");
        assertThat(applicationInfoSk.items().get(0).text()).isEqualTo("Rezanie a hranovanie drevotriesky presne na mieru, laminované drevotrieskové dosky a ABS hrany vo viac ako 150 dekoroch.");
        assertThat(applicationInfoSk.items().get(0).imageFileName()).isEqualTo("cutting-and-edging.png");

        // company-info
        final CompanyInfoSo enCompanyInfoDto = restTemplate.getForObject(getURI("/ui/company-info", enQueryParams()), CompanyInfoSo.class);
        assertThat(enCompanyInfoDto).isNotNull();
        assertThat(enCompanyInfoDto.name()).isEqualTo("WIWA, Ltd.");
        assertThat(enCompanyInfoDto.street()).isEqualTo("Street 12/4567");
        assertThat(enCompanyInfoDto.city()).isEqualTo("Zvolen");
        assertThat(enCompanyInfoDto.zipCode()).isEqualTo("960 01");
        assertThat(enCompanyInfoDto.state()).isEqualTo("Slovakia");
        assertThat(enCompanyInfoDto.phone()).isEqualTo("+421 111 111 111");
        assertThat(enCompanyInfoDto.mail()).isEqualTo("mail@domain.sk");
        assertThat(enCompanyInfoDto.businessId()).isEqualTo("11 111 111");
        assertThat(enCompanyInfoDto.taxId()).isEqualTo("1111111111");
        assertThat(enCompanyInfoDto.vatRegNo()).isEqualTo("SK1111111111");
        assertThat(enCompanyInfoDto.commercialRegisterInfo()).isEqualTo("The company is registered in the Commercial Register of the District Court in Zvolen, section Sro, insert number 11111/P");
        assertThat(enCompanyInfoDto.mapUrl()).isEqualTo("https://maps.google.com/maps?q=Zvolen&t=&z=13&ie=UTF8&iwloc=&output=embed");

        final CompanyInfoSo skCompanyInfoDto = restTemplate.getForObject(getURI("/ui/company-info", skQueryParams()), CompanyInfoSo.class);
        assertThat(skCompanyInfoDto).isNotNull();
        assertThat(skCompanyInfoDto.name()).isEqualTo("WIWA, s.r.o.");
        assertThat(skCompanyInfoDto.street()).isEqualTo("Ulica 12/4567");
        assertThat(skCompanyInfoDto.city()).isEqualTo("Zvolen");
        assertThat(skCompanyInfoDto.zipCode()).isEqualTo("960 01");
        assertThat(skCompanyInfoDto.state()).isEqualTo("Slovensko");
        assertThat(skCompanyInfoDto.phone()).isEqualTo("+421 111 111 111");
        assertThat(skCompanyInfoDto.mail()).isEqualTo("mail@domain.sk");
        assertThat(skCompanyInfoDto.businessId()).isEqualTo("11 111 111");
        assertThat(skCompanyInfoDto.taxId()).isEqualTo("1111111111");
        assertThat(skCompanyInfoDto.vatRegNo()).isEqualTo("SK1111111111");
        assertThat(skCompanyInfoDto.commercialRegisterInfo()).isEqualTo("Spoločnosť je zapísaná v Obchodnom registri Okresného súdu vo Zvolene, oddiel Sro, vložka číslo 11111/P");
        assertThat(skCompanyInfoDto.mapUrl()).isEqualTo("https://maps.google.com/maps?q=Zvolen&t=&z=13&ie=UTF8&iwloc=&output=embed");

        // cookies-info
        final String cookiesInfoEn = restTemplate.getForObject(getURI("/ui/cookies-info", enQueryParams()), String.class);
        assertThat(cookiesInfoEn).isEqualTo(
                applicationPropertyRepository.getApplicationProperty(
                        WiwaProperty.APP_COOKIES_INFO.getGroup(),
                        WiwaProperty.APP_COOKIES_INFO.getKey(),
                        "en"
                ).orElseThrow().value()
        );

        final String cookiesInfoSk = restTemplate.getForObject(getURI("/ui/cookies-info", skQueryParams()), String.class);
        assertThat(cookiesInfoSk).isEqualTo(
                applicationPropertyRepository.getApplicationProperty(
                        WiwaProperty.APP_COOKIES_INFO.getGroup(),
                        WiwaProperty.APP_COOKIES_INFO.getKey(),
                        "sk"
                ).orElseThrow().value()
        );

        // gdpr-info
        final String gdprInfoEn = restTemplate.getForObject(getURI("/ui/gdpr-info", enQueryParams()), String.class);
        assertThat(gdprInfoEn).isEqualTo(
                applicationPropertyRepository.getApplicationProperty(
                        WiwaProperty.APP_GDPR_INFO.getGroup(),
                        WiwaProperty.APP_GDPR_INFO.getKey(),
                        "en"
                ).orElseThrow().value()
        );

        final String gdprInfoSk = restTemplate.getForObject(getURI("/ui/gdpr-info", skQueryParams()), String.class);
        assertThat(gdprInfoSk).isEqualTo(
                applicationPropertyRepository.getApplicationProperty(
                        WiwaProperty.APP_GDPR_INFO.getGroup(),
                        WiwaProperty.APP_GDPR_INFO.getKey(),
                        "sk"
                ).orElseThrow().value()
        );

        // working-hours
        final String workingHoursEn = restTemplate.getForObject(getURI("/ui/working-hours", enQueryParams()), String.class);
        assertThat(workingHoursEn).isEqualTo(
                applicationPropertyRepository.getApplicationProperty(
                        WiwaProperty.APP_WORKING_HOURS.getGroup(),
                        WiwaProperty.APP_WORKING_HOURS.getKey(),
                        "en"
                ).orElseThrow().value()
        );

        final String workingHoursSk = restTemplate.getForObject(getURI("/ui/working-hours", skQueryParams()), String.class);
        assertThat(workingHoursSk).isEqualTo(
                applicationPropertyRepository.getApplicationProperty(
                        WiwaProperty.APP_WORKING_HOURS.getGroup(),
                        WiwaProperty.APP_WORKING_HOURS.getKey(),
                        "sk"
                ).orElseThrow().value()
        );
    }
}
