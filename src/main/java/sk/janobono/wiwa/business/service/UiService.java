package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.business.model.ui.ApplicationInfoItemSo;
import sk.janobono.wiwa.business.model.ui.ApplicationInfoSo;
import sk.janobono.wiwa.business.model.ui.CompanyInfoSo;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.mapper.ApplicationImageMapper;
import sk.janobono.wiwa.model.ApplicationImage;
import sk.janobono.wiwa.model.ResourceEntity;
import sk.janobono.wiwa.model.WiwaProperty;
import sk.janobono.wiwa.dal.domain.ApplicationImageDo;
import sk.janobono.wiwa.dal.repository.ApplicationImageRepository;

import java.io.IOException;
import java.util.ArrayList;

@RequiredArgsConstructor
@Service
public class UiService {
    private final CommonConfigProperties commonConfigProperties;
    private final ApplicationImageMapper applicationImageMapper;
    private final ImageUtil imageUtil;
    private final ApplicationImageService applicationImageService;
    private final ApplicationPropertyService applicationPropertyService;
    private final ApplicationImageRepository applicationImageRepository;

    public String getDefaultLocale() {
        return commonConfigProperties.defaultLocale();
    }

    public ResourceEntity getLogo() {
        return applicationImageService.getApplicationImage("logo.png");
    }

    public String getTitle() {
        return applicationPropertyService.getProperty(WiwaProperty.APP_TITLE);
    }

    public String getWelcomeText() {
        return applicationPropertyService.getProperty(WiwaProperty.APP_WELCOME_TEXT);
    }

    public ApplicationInfoSo getApplicationInfo() {
        final ApplicationInfoSo result = new ApplicationInfoSo(new ArrayList<>());
        final int count = Integer.parseInt(applicationPropertyService.getProperty(WiwaProperty.APP_INFO_SLIDE_COUNT));
        for (int i = 0; i < count; i++) {
            result.items().add(new ApplicationInfoItemSo(applicationPropertyService.getProperty(WiwaProperty.APP_INFO_SLIDE_X_TITLE, i),
                    applicationPropertyService.getProperty(WiwaProperty.APP_INFO_SLIDE_X_TEXT, i),
                    applicationPropertyService.getProperty(WiwaProperty.APP_INFO_SLIDE_X_IMAGE, i)));
        }
        return result;
    }

    public CompanyInfoSo getCompanyInfo() {
        return new CompanyInfoSo(applicationPropertyService.getProperty(WiwaProperty.COMPANY_NAME),
                applicationPropertyService.getProperty(WiwaProperty.COMPANY_STREET),
                applicationPropertyService.getProperty(WiwaProperty.COMPANY_CITY),
                applicationPropertyService.getProperty(WiwaProperty.COMPANY_ZIP_CODE),
                applicationPropertyService.getProperty(WiwaProperty.COMPANY_STATE),
                applicationPropertyService.getProperty(WiwaProperty.COMPANY_PHONE),
                applicationPropertyService.getProperty(WiwaProperty.COMPANY_MAIL),
                applicationPropertyService.getProperty(WiwaProperty.COMPANY_BUSINESS_ID),
                applicationPropertyService.getProperty(WiwaProperty.COMPANY_TAX_ID),
                applicationPropertyService.getProperty(WiwaProperty.COMPANY_VAT_REG_NO),
                applicationPropertyService.getProperty(WiwaProperty.COMPANY_COMMERCIAL_REGISTER_INFO),
                applicationPropertyService.getProperty(WiwaProperty.COMPANY_MAP_URL));
    }

    public String getCookiesInfo() {
        return applicationPropertyService.getProperty(WiwaProperty.APP_COOKIES_INFO);
    }

    public String getGdprInfo() {
        return applicationPropertyService.getProperty(WiwaProperty.APP_GDPR_INFO);
    }

    public String getWorkingHours() {
        return applicationPropertyService.getProperty(WiwaProperty.APP_WORKING_HOURS);
    }

    @Transactional
    public ApplicationImage setLogo(final MultipartFile multipartFile) {
        final String fileName = "logo.png";
        final String fileType = multipartFile.getContentType() != null ? multipartFile.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        if (!fileType.equals(MediaType.IMAGE_PNG_VALUE)) {
            throw WiwaException.APPLICATION_IMAGE_NOT_SUPPORTED.exception("Unsupported logo file type {0}", fileType);
        }

        final byte[] data;
        try {
            data = multipartFile.getBytes();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        final ApplicationImageDo applicationImageDo = new ApplicationImageDo();
        applicationImageDo.setFileName(fileName);
        applicationImageDo.setFileType(fileType);
        applicationImageDo.setThumbnail(imageUtil.scaleImage(
                fileType,
                data,
                commonConfigProperties.maxThumbnailResolution(), commonConfigProperties.maxThumbnailResolution())
        );
        applicationImageDo.setData(imageUtil.scaleImage(
                fileType,
                data,
                commonConfigProperties.maxImageResolution(), commonConfigProperties.maxImageResolution())
        );

        return applicationImageMapper.map(applicationImageRepository.save(applicationImageDo));
    }

    public String setTitle(final String title) {
        return applicationPropertyService.setApplicationProperty(WiwaProperty.APP_TITLE.getGroup(), WiwaProperty.APP_TITLE.getKey(), title);
    }

    public String setWelcomeText(final String welcomeText) {
        return applicationPropertyService.setApplicationProperty(WiwaProperty.APP_WELCOME_TEXT.getGroup(), WiwaProperty.APP_WELCOME_TEXT.getKey(), welcomeText);
    }

    @Transactional
    public ApplicationInfoSo setApplicationInfo(final ApplicationInfoSo applicationInfo) {
        applicationPropertyService.setApplicationProperty(WiwaProperty.APP_INFO_SLIDE_COUNT.getGroup(), WiwaProperty.APP_INFO_SLIDE_COUNT.getKey(), Integer.toString(applicationInfo.items().size()));
        for (int i = 0; i < applicationInfo.items().size(); i++) {
            final ApplicationInfoItemSo applicationInfoItemDto = applicationInfo.items().get(i);
            applicationPropertyService.setApplicationProperty(WiwaProperty.APP_INFO_SLIDE_X_TITLE.getGroup(), WiwaProperty.APP_INFO_SLIDE_X_TITLE.getKey(i), applicationInfoItemDto.title());
            applicationPropertyService.setApplicationProperty(WiwaProperty.APP_INFO_SLIDE_X_TEXT.getGroup(), WiwaProperty.APP_INFO_SLIDE_X_TEXT.getKey(i), applicationInfoItemDto.text());
            applicationPropertyService.setApplicationProperty(WiwaProperty.APP_INFO_SLIDE_X_IMAGE.getGroup(), WiwaProperty.APP_INFO_SLIDE_X_IMAGE.getKey(i), applicationInfoItemDto.imageFileName());
        }
        return applicationInfo;
    }

    @Transactional
    public CompanyInfoSo setCompanyInfo(final CompanyInfoSo companyInfo) {
        applicationPropertyService.setApplicationProperty(WiwaProperty.COMPANY_NAME.getGroup(), WiwaProperty.COMPANY_NAME.getKey(), companyInfo.name());
        applicationPropertyService.setApplicationProperty(WiwaProperty.COMPANY_STREET.getGroup(), WiwaProperty.COMPANY_STREET.getKey(), companyInfo.street());
        applicationPropertyService.setApplicationProperty(WiwaProperty.COMPANY_CITY.getGroup(), WiwaProperty.COMPANY_CITY.getKey(), companyInfo.city());
        applicationPropertyService.setApplicationProperty(WiwaProperty.COMPANY_ZIP_CODE.getGroup(), WiwaProperty.COMPANY_ZIP_CODE.getKey(), companyInfo.zipCode());
        applicationPropertyService.setApplicationProperty(WiwaProperty.COMPANY_STATE.getGroup(), WiwaProperty.COMPANY_STATE.getKey(), companyInfo.state());
        applicationPropertyService.setApplicationProperty(WiwaProperty.COMPANY_PHONE.getGroup(), WiwaProperty.COMPANY_PHONE.getKey(), companyInfo.phone());
        applicationPropertyService.setApplicationProperty(WiwaProperty.COMPANY_MAIL.getGroup(), WiwaProperty.COMPANY_MAIL.getKey(), companyInfo.mail());
        applicationPropertyService.setApplicationProperty(WiwaProperty.COMPANY_BUSINESS_ID.getGroup(), WiwaProperty.COMPANY_BUSINESS_ID.getKey(), companyInfo.businessId());
        applicationPropertyService.setApplicationProperty(WiwaProperty.COMPANY_TAX_ID.getGroup(), WiwaProperty.COMPANY_TAX_ID.getKey(), companyInfo.taxId());
        applicationPropertyService.setApplicationProperty(WiwaProperty.COMPANY_VAT_REG_NO.getGroup(), WiwaProperty.COMPANY_VAT_REG_NO.getKey(), companyInfo.vatRegNo());
        applicationPropertyService.setApplicationProperty(WiwaProperty.COMPANY_COMMERCIAL_REGISTER_INFO.getGroup(), WiwaProperty.COMPANY_COMMERCIAL_REGISTER_INFO.getKey(), companyInfo.commercialRegisterInfo());
        applicationPropertyService.setApplicationProperty(WiwaProperty.COMPANY_MAP_URL.getGroup(), WiwaProperty.COMPANY_MAP_URL.getKey(), companyInfo.mapUrl());
        return companyInfo;
    }

    public String setCookiesInfo(final String cookiesInfo) {
        return applicationPropertyService.setApplicationProperty(WiwaProperty.APP_COOKIES_INFO.getGroup(), WiwaProperty.APP_COOKIES_INFO.getKey(), cookiesInfo);
    }

    public String setGdprInfo(final String gdprInfo) {
        return applicationPropertyService.setApplicationProperty(WiwaProperty.APP_GDPR_INFO.getGroup(), WiwaProperty.APP_GDPR_INFO.getKey(), gdprInfo);
    }

    public String setWorkingHours(final String workingHours) {
        return applicationPropertyService.setApplicationProperty(WiwaProperty.APP_WORKING_HOURS.getGroup(), WiwaProperty.APP_WORKING_HOURS.getKey(), workingHours);
    }
}
