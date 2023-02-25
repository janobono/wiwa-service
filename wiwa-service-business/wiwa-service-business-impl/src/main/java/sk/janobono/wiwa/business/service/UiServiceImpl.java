package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.business.component.LocalizedDataUtil;
import sk.janobono.wiwa.business.mapper.ApplicationImageMapper;
import sk.janobono.wiwa.business.model.ApplicationImageSo;
import sk.janobono.wiwa.business.model.ui.*;
import sk.janobono.wiwa.common.component.ImageUtil;
import sk.janobono.wiwa.common.config.CommonConfigProperties;
import sk.janobono.wiwa.common.exception.WiwaException;
import sk.janobono.wiwa.common.model.ResourceEntitySo;
import sk.janobono.wiwa.common.model.WiwaProperty;
import sk.janobono.wiwa.dal.domain.ApplicationImageDo;
import sk.janobono.wiwa.dal.repository.ApplicationImageRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UiServiceImpl implements UiService {

    private final CommonConfigProperties commonConfigProperties;
    private final ImageUtil imageUtil;
    private final ApplicationImageService applicationImageService;
    private final ApplicationPropertyService applicationPropertyService;
    private final ApplicationImageMapper applicationImageMapper;
    private final ApplicationImageRepository applicationImageRepository;
    private final LocalizedDataUtil localizedDataUtil;

    public ResourceEntitySo getLogo() {
        log.debug("getLogo()");
        return applicationImageService.getApplicationImage("logo.png");
    }

    public String getTitle() {
        log.debug("getTitle()");
        return applicationPropertyService.getProperty(WiwaProperty.APP_TITLE);
    }

    public String getWelcomeText() {
        log.debug("getWelcomeText()");
        return applicationPropertyService.getProperty(WiwaProperty.APP_WELCOME_TEXT);
    }

    public ApplicationInfoSo getApplicationInfo() {
        log.debug("getApplicationInfo()");
        ApplicationInfoSo result = new ApplicationInfoSo(new ArrayList<>());
        int count = Integer.parseInt(applicationPropertyService.getProperty(WiwaProperty.APP_INFO_SLIDE_COUNT));
        for (int i = 0; i < count; i++) {
            result.items().add(new ApplicationInfoItemSo(
                    applicationPropertyService.getProperty(WiwaProperty.APP_INFO_SLIDE_X_TITLE, i),
                    applicationPropertyService.getProperty(WiwaProperty.APP_INFO_SLIDE_X_TEXT, i),
                    applicationPropertyService.getProperty(WiwaProperty.APP_INFO_SLIDE_X_IMAGE, i)
            ));
        }
        log.debug("getApplicationInfo()={}", result);
        return result;
    }

    public CompanyInfoSo getCompanyInfo() {
        log.debug("getCompanyInfo()");
        CompanyInfoSo result = new CompanyInfoSo(
                applicationPropertyService.getProperty(WiwaProperty.COMPANY_NAME),
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
                applicationPropertyService.getProperty(WiwaProperty.COMPANY_MAP_URL)
        );
        log.debug("getCompanyInfo()={}", result);
        return result;
    }

    public String getCookiesInfo() {
        log.debug("getCookiesInfo()");
        return applicationPropertyService.getProperty(WiwaProperty.APP_COOKIES_INFO);
    }

    public String getGdprInfo() {
        log.debug("getGdprInfo()");
        return applicationPropertyService.getProperty(WiwaProperty.APP_GDPR_INFO);
    }

    public String getWorkingHours() {
        log.debug("getWorkingHours()");
        return applicationPropertyService.getProperty(WiwaProperty.APP_WORKING_HOURS);
    }

    public ApplicationImageSo setLogo(MultipartFile multipartFile) {
        log.debug("setLogo({})", multipartFile.getOriginalFilename());

        String fileName = "logo.png";
        String fileType = multipartFile.getContentType() != null
                ? multipartFile.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        if (!fileType.equals(MediaType.IMAGE_PNG_VALUE)) {
            throw WiwaException.APPLICATION_IMAGE_NOT_SUPPORTED.exception("Unsupported logo file type {0}", fileType);
        }

        byte[] data;
        try {
            data = multipartFile.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ApplicationImageDo applicationImageDo = new ApplicationImageDo(
                fileName,
                fileType,
                imageUtil.scaleImage(
                        fileType,
                        data,
                        commonConfigProperties.maxThumbnailResolution(),
                        commonConfigProperties.maxThumbnailResolution()
                ),
                imageUtil.scaleImage(
                        fileType,
                        data,
                        commonConfigProperties.maxImageResolution(),
                        commonConfigProperties.maxImageResolution()
                )
        );

        ApplicationImageSo result;
        if (applicationImageRepository.exists(fileName)) {
            result = applicationImageMapper.map(applicationImageRepository.setApplicationImage(applicationImageDo));
        } else {
            result = applicationImageMapper.map(applicationImageRepository.addApplicationImage(applicationImageDo));
        }
        log.debug("setLogo({})={}", multipartFile.getOriginalFilename(), result);
        return result;
    }

    public LocalizedDataSo<String> setTitle(LocalizedDataSo<String> data) {
        log.debug("setTitle({})", data);
        LocalizedDataSo<String> result = localizedDataUtil.saveLocalizedData(
                data, WiwaProperty.APP_TITLE.getGroup(), WiwaProperty.APP_TITLE.getKey()
        );
        log.debug("setTitle({})={}", data, result);
        return result;
    }

    public LocalizedDataSo<String> setWelcomeText(LocalizedDataSo<String> data) {
        log.debug("setWelcomeText({})", data);
        LocalizedDataSo<String> result = localizedDataUtil.saveLocalizedData(
                data, WiwaProperty.APP_WELCOME_TEXT.getGroup(), WiwaProperty.APP_WELCOME_TEXT.getKey()
        );
        log.debug("setWelcomeText({})={}", data, result);
        return result;
    }

    public LocalizedDataSo<ApplicationInfoSo> setApplicationInfo(LocalizedDataSo<ApplicationInfoSo> data) {
        log.debug("setApplicationInfo({})", data);
        LocalizedDataSo<ApplicationInfoSo> result = new LocalizedDataSo<>(new ArrayList<>());
        for (LocalizedDataItemSo<ApplicationInfoSo> localizedDataItem : data.items()) {
            applicationPropertyService.setApplicationProperty(
                    WiwaProperty.APP_INFO_SLIDE_COUNT.getGroup(),
                    WiwaProperty.APP_INFO_SLIDE_COUNT.getKey(),
                    "",
                    Integer.toString(localizedDataItem.data().items().size())
            );
            List<ApplicationInfoItemSo> items = new ArrayList<>();
            for (int i = 0; i < localizedDataItem.data().items().size(); i++) {
                ApplicationInfoItemSo applicationInfoItemDto = localizedDataItem.data().items().get(i);
                items.add(new ApplicationInfoItemSo(
                        applicationPropertyService.setApplicationProperty(
                                WiwaProperty.APP_INFO_SLIDE_X_TITLE.getGroup(),
                                WiwaProperty.APP_INFO_SLIDE_X_TITLE.getKey(i),
                                localizedDataItem.language(),
                                applicationInfoItemDto.title()
                        ),
                        applicationPropertyService.setApplicationProperty(
                                WiwaProperty.APP_INFO_SLIDE_X_TEXT.getGroup(),
                                WiwaProperty.APP_INFO_SLIDE_X_TEXT.getKey(i),
                                localizedDataItem.language(),
                                applicationInfoItemDto.text()
                        ),
                        applicationPropertyService.setApplicationProperty(
                                WiwaProperty.APP_INFO_SLIDE_X_IMAGE.getGroup(),
                                WiwaProperty.APP_INFO_SLIDE_X_IMAGE.getKey(i),
                                "",
                                applicationInfoItemDto.imageFileName()
                        )
                ));
            }
            result.items().add(new LocalizedDataItemSo<>(localizedDataItem.language(), new ApplicationInfoSo(items)));
        }
        log.debug("setApplicationInfo({})={}", data, result);
        return result;
    }

    public LocalizedDataSo<CompanyInfoSo> setCompanyInfo(LocalizedDataSo<CompanyInfoSo> data) {
        log.debug("setCompanyInfo({})", data);
        LocalizedDataSo<CompanyInfoSo> result = new LocalizedDataSo<>(new ArrayList<>());
        for (LocalizedDataItemSo<CompanyInfoSo> localizedDataItem : data.items()) {
            result.items().add(new LocalizedDataItemSo<>(localizedDataItem.language(), new CompanyInfoSo(
                    applicationPropertyService.setApplicationProperty(
                            WiwaProperty.COMPANY_NAME.getGroup(),
                            WiwaProperty.COMPANY_NAME.getKey(),
                            localizedDataItem.language(),
                            localizedDataItem.data().name()
                    ),
                    applicationPropertyService.setApplicationProperty(
                            WiwaProperty.COMPANY_STREET.getGroup(),
                            WiwaProperty.COMPANY_STREET.getKey(),
                            localizedDataItem.language(),
                            localizedDataItem.data().street()
                    ),
                    applicationPropertyService.setApplicationProperty(
                            WiwaProperty.COMPANY_CITY.getGroup(),
                            WiwaProperty.COMPANY_CITY.getKey(),
                            localizedDataItem.language(),
                            localizedDataItem.data().city()
                    ),
                    applicationPropertyService.setApplicationProperty(
                            WiwaProperty.COMPANY_ZIP_CODE.getGroup(),
                            WiwaProperty.COMPANY_ZIP_CODE.getKey(),
                            "",
                            localizedDataItem.data().zipCode()
                    ),
                    applicationPropertyService.setApplicationProperty(
                            WiwaProperty.COMPANY_STATE.getGroup(),
                            WiwaProperty.COMPANY_STATE.getKey(),
                            localizedDataItem.language(),
                            localizedDataItem.data().state()
                    ),
                    applicationPropertyService.setApplicationProperty(
                            WiwaProperty.COMPANY_PHONE.getGroup(),
                            WiwaProperty.COMPANY_PHONE.getKey(),
                            "",
                            localizedDataItem.data().phone()
                    ),
                    applicationPropertyService.setApplicationProperty(
                            WiwaProperty.COMPANY_MAIL.getGroup(),
                            WiwaProperty.COMPANY_MAIL.getKey(),
                            "",
                            localizedDataItem.data().mail()
                    ),
                    applicationPropertyService.setApplicationProperty(
                            WiwaProperty.COMPANY_BUSINESS_ID.getGroup(),
                            WiwaProperty.COMPANY_BUSINESS_ID.getKey(),
                            "",
                            localizedDataItem.data().businessId()
                    ),
                    applicationPropertyService.setApplicationProperty(
                            WiwaProperty.COMPANY_TAX_ID.getGroup(),
                            WiwaProperty.COMPANY_TAX_ID.getKey(),
                            "",
                            localizedDataItem.data().taxId()
                    ),
                    applicationPropertyService.setApplicationProperty(
                            WiwaProperty.COMPANY_VAT_REG_NO.getGroup(),
                            WiwaProperty.COMPANY_VAT_REG_NO.getKey(),
                            "",
                            localizedDataItem.data().vatRegNo()
                    ),
                    applicationPropertyService.setApplicationProperty(
                            WiwaProperty.COMPANY_COMMERCIAL_REGISTER_INFO.getGroup(),
                            WiwaProperty.COMPANY_COMMERCIAL_REGISTER_INFO.getKey(),
                            localizedDataItem.language(),
                            localizedDataItem.data().commercialRegisterInfo()
                    ),
                    applicationPropertyService.setApplicationProperty(
                            WiwaProperty.COMPANY_MAP_URL.getGroup(),
                            WiwaProperty.COMPANY_MAP_URL.getKey(),
                            "",
                            localizedDataItem.data().mapUrl()
                    )
            )));
        }
        log.debug("setCompanyInfo({})={}", data, result);
        return result;
    }

    public LocalizedDataSo<String> setCookiesInfo(LocalizedDataSo<String> data) {
        log.debug("setCookiesInfo({})", data);
        LocalizedDataSo<String> result = localizedDataUtil.saveLocalizedData(
                data, WiwaProperty.APP_COOKIES_INFO.getGroup(), WiwaProperty.APP_COOKIES_INFO.getKey()
        );
        log.debug("setCookiesInfo({})={}", data, result);
        return result;
    }

    public LocalizedDataSo<String> setGdprInfo(LocalizedDataSo<String> data) {
        log.debug("setGdprInfo({})", data);
        LocalizedDataSo<String> result = localizedDataUtil.saveLocalizedData(
                data, WiwaProperty.APP_GDPR_INFO.getGroup(), WiwaProperty.APP_GDPR_INFO.getKey()
        );
        log.debug("setGdprInfo({})={}", data, result);
        return result;
    }

    public LocalizedDataSo<String> setWorkingHours(LocalizedDataSo<String> data) {
        log.debug("setWorkingHours({})", data);
        LocalizedDataSo<String> result = localizedDataUtil.saveLocalizedData(
                data, WiwaProperty.APP_WORKING_HOURS.getGroup(), WiwaProperty.APP_WORKING_HOURS.getKey()
        );
        log.debug("setWorkingHours({})={}", data, result);
        return result;
    }
}
