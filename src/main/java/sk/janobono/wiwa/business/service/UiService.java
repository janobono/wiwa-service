package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.business.mapper.ApplicationImageDataMapper;
import sk.janobono.wiwa.business.model.ApplicationImageData;
import sk.janobono.wiwa.business.model.ApplicationImageInfoData;
import sk.janobono.wiwa.business.model.captcha.CaptchaData;
import sk.janobono.wiwa.business.model.ui.ApplicationPropertiesData;
import sk.janobono.wiwa.business.model.ui.CompanyInfoData;
import sk.janobono.wiwa.business.model.ui.UnitData;
import sk.janobono.wiwa.component.Captcha;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.config.JwtConfigProperties;
import sk.janobono.wiwa.dal.domain.ApplicationImageDo;
import sk.janobono.wiwa.dal.repository.ApplicationImageRepository;
import sk.janobono.wiwa.dal.repository.BoardImageRepository;
import sk.janobono.wiwa.dal.repository.EdgeImageRepository;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.Unit;
import sk.janobono.wiwa.model.WiwaProperty;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
public class UiService {

    private final CommonConfigProperties commonConfigProperties;
    private final JwtConfigProperties jwtConfigProperties;

    private final Captcha captcha;
    private final ImageUtil imageUtil;
    private final ScDf scDf;

    private final ApplicationImageDataMapper applicationImageDataMapper;

    private final ApplicationPropertyService applicationPropertyService;
    private final ApplicationImageRepository applicationImageRepository;
    private final BoardImageRepository boardImageRepository;
    private final EdgeImageRepository edgeImageRepository;

    public CaptchaData getCaptcha() {
        final String text = captcha.generateText();
        final String image = "data:image/png;base64," + Base64.getEncoder().encodeToString(captcha.generateImage(text));
        final String token = captcha.generateToken(text);
        return new CaptchaData(token, image);
    }

    public Page<ApplicationImageInfoData> getApplicationImages(final Pageable pageable) {
        return applicationImageRepository.findAll(pageable)
                .map(applicationImageDataMapper::mapToData);
    }

    public ApplicationImageData getApplicationImage(final String fileName) {
        return applicationImageRepository.findById(scDf.toStripAndLowerCase(fileName))
                .map(applicationImageDataMapper::mapToData)
                .orElseGet(() -> new ApplicationImageData(
                        fileName,
                        MediaType.IMAGE_PNG_VALUE,
                        imageUtil.generateMessageImage(null),
                        imageUtil.generateMessageImage(null)
                ));
    }

    public ApplicationImageData getBoardImage(final Long boardId, final String fileName) {
        return boardImageRepository.findByBoardIdAndFileName(boardId, scDf.toStripAndLowerCase(fileName))
                .map(applicationImageDataMapper::mapToData)
                .orElseGet(() -> new ApplicationImageData(
                        fileName,
                        MediaType.IMAGE_PNG_VALUE,
                        imageUtil.generateMessageImage(null),
                        imageUtil.generateMessageImage(null)
                ));
    }

    public ApplicationImageData getEdgeImage(final Long edgeId, final String fileName) {
        return edgeImageRepository.findByEdgeIdAndFileName(edgeId, scDf.toStripAndLowerCase(fileName))
                .map(applicationImageDataMapper::mapToData)
                .orElseGet(() -> new ApplicationImageData(
                        fileName,
                        MediaType.IMAGE_PNG_VALUE,
                        imageUtil.generateMessageImage(null),
                        imageUtil.generateMessageImage(null)
                ));
    }

    public ApplicationImageInfoData setApplicationImage(final MultipartFile multipartFile) {
        final String fileName = scDf.toStripAndLowerCase(multipartFile.getOriginalFilename());
        final String fileType = Optional.ofNullable(multipartFile.getContentType())
                .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        if (!imageUtil.isImageFile(fileType)) {
            throw WiwaException.APPLICATION_IMAGE_NOT_SUPPORTED.exception("Unsupported file type {0}", fileType);
        }

        final ApplicationImageDo applicationImageDo = ApplicationImageDo.builder()
                .fileName(fileName)
                .fileType(fileType)
                .thumbnail(imageUtil.scaleImage(
                        fileType,
                        imageUtil.getFileData(multipartFile),
                        commonConfigProperties.maxThumbnailResolution(),
                        commonConfigProperties.maxThumbnailResolution()
                ))
                .data(imageUtil.scaleImage(
                        fileType,
                        imageUtil.getFileData(multipartFile),
                        commonConfigProperties.maxThumbnailResolution(),
                        commonConfigProperties.maxThumbnailResolution()
                ))
                .build();

        return applicationImageDataMapper.mapToInfoData(applicationImageRepository.save(applicationImageDo));
    }

    public void deleteApplicationImage(final String fileName) {
        applicationImageRepository.deleteById(fileName);
    }

    public ApplicationPropertiesData getApplicationProperties() {
        return new ApplicationPropertiesData(
                commonConfigProperties.defaultLocale(),
                commonConfigProperties.appTitle(),
                commonConfigProperties.appDescription(),
                jwtConfigProperties.expiration()
        );
    }

    public ApplicationImageData getLogo() {
        return getApplicationImage("logo.png");
    }

    public String getTitle() {
        return applicationPropertyService.getProperty(WiwaProperty.APP_TITLE);
    }

    public String getWelcomeText() {
        return applicationPropertyService.getProperty(WiwaProperty.APP_WELCOME_TEXT);
    }

    public List<String> getApplicationInfo() {
        final List<String> result = new ArrayList<>();
        final int count = Integer.parseInt(applicationPropertyService.getProperty(WiwaProperty.APP_INFO_SLIDE_COUNT));
        for (int i = 0; i < count; i++) {
            result.add(applicationPropertyService.getProperty(WiwaProperty.APP_INFO_SLIDE_X_TEXT, i));
        }
        return result;
    }

    public CompanyInfoData getCompanyInfo() {
        return new CompanyInfoData(applicationPropertyService.getProperty(WiwaProperty.COMPANY_NAME),
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

    public String getBusinessConditions() {
        return applicationPropertyService.getProperty(WiwaProperty.APP_BUSINESS_CONDITIONS);
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

    public List<UnitData> getUnits() {
        return applicationPropertyService.getProperties(WiwaProperty.UNIT_GROUP.getGroup()).entrySet().stream()
                .map(entry -> new UnitData(Unit.valueOf(entry.getKey()), entry.getValue()))
                .sorted(Comparator.comparingInt(o -> o.id().ordinal()))
                .toList();
    }

    public ApplicationImageInfoData setLogo(final MultipartFile multipartFile) {
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

        final ApplicationImageDo applicationImageDo = ApplicationImageDo.builder()
                .fileName(fileName)
                .fileType(fileType)
                .thumbnail(imageUtil.scaleImage(
                        fileType,
                        data,
                        commonConfigProperties.maxThumbnailResolution(), commonConfigProperties.maxThumbnailResolution())
                )
                .data(imageUtil.scaleImage(
                        fileType,
                        data,
                        commonConfigProperties.maxImageResolution(), commonConfigProperties.maxImageResolution()))
                .build();

        return applicationImageDataMapper.mapToInfoData(applicationImageRepository.save(applicationImageDo));
    }

    public String setTitle(final String title) {
        return applicationPropertyService.setApplicationProperty(WiwaProperty.APP_TITLE.getGroup(), WiwaProperty.APP_TITLE.getKey(), title);
    }

    public String setWelcomeText(final String welcomeText) {
        return applicationPropertyService.setApplicationProperty(WiwaProperty.APP_WELCOME_TEXT.getGroup(), WiwaProperty.APP_WELCOME_TEXT.getKey(), welcomeText);
    }

    public List<String> setApplicationInfo(final List<String> applicationInfo) {
        applicationPropertyService.setApplicationProperty(WiwaProperty.APP_INFO_SLIDE_COUNT.getGroup(), WiwaProperty.APP_INFO_SLIDE_COUNT.getKey(), Integer.toString(applicationInfo.size()));
        for (int i = 0; i < applicationInfo.size(); i++) {
            applicationPropertyService.setApplicationProperty(WiwaProperty.APP_INFO_SLIDE_X_TEXT.getGroup(), WiwaProperty.APP_INFO_SLIDE_X_TEXT.getKey(i), applicationInfo.get(i));
        }
        return applicationInfo;
    }

    public CompanyInfoData setCompanyInfo(final CompanyInfoData companyInfo) {
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

    public String setBusinessConditions(final String businessConditions) {
        return applicationPropertyService.setApplicationProperty(WiwaProperty.APP_BUSINESS_CONDITIONS.getGroup(), WiwaProperty.APP_BUSINESS_CONDITIONS.getKey(), businessConditions);
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

    public List<UnitData> setUnits(final List<UnitData> data) {
        for (final UnitData unit : data) {
            applicationPropertyService.setApplicationProperty(WiwaProperty.UNIT_GROUP.getGroup(), unit.id().name(), unit.value());
        }
        return data;
    }
}
