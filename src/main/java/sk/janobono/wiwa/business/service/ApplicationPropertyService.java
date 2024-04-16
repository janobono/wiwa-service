package sk.janobono.wiwa.business.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.model.*;
import sk.janobono.wiwa.business.service.util.PropertyUtilService;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.config.JwtConfigProperties;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ApplicationPropertyService {

    private static final String TITLE = "TITLE";
    private static final String WELCOME_TEXT = "WELCOME_TEXT";
    private static final String APP_INFO = "APP_INFO";
    private static final String COMPANY_INFO = "COMPANY_INFO";
    private static final String UNITS = "UNITS";
    private static final String VAT_RATE = "VAT_RATE";
    private static final String BUSINESS_CONDITIONS = "BUSINESS_CONDITIONS";
    private static final String COOKIES_INFO = "COOKIES_INFO";
    private static final String GDPR_INFO = "GDPR_INFO";
    private static final String WORKING_HOURS = "WORKING_HOURS";
    private static final String SIGN_UP_MAIL = "SIGN_UP_MAIL";
    private static final String RESET_PASSWORD_MAIL = "RESET_PASSWORD_MAIL";

    private final ObjectMapper objectMapper;

    private final CommonConfigProperties commonConfigProperties;
    private final JwtConfigProperties jwtConfigProperties;

    private final PropertyUtilService propertyUtilService;

    public ApplicationPropertiesData getApplicationProperties() {
        return new ApplicationPropertiesData(
                commonConfigProperties.defaultLocale(),
                commonConfigProperties.appTitle(),
                commonConfigProperties.appDescription(),
                jwtConfigProperties.expiration()
        );
    }

    public String getTitle() {
        return propertyUtilService.getProperty(TITLE);
    }

    public String setTitle(final String title) {
        propertyUtilService.setProperty(TITLE, title);
        return title;
    }

    public String getWelcomeText() {
        return propertyUtilService.getProperty(WELCOME_TEXT);
    }

    public String setWelcomeText(final String welcomeText) {
        propertyUtilService.setProperty(WELCOME_TEXT, welcomeText);
        return welcomeText;
    }

    public List<String> getApplicationInfo() {
        return propertyUtilService.getProperty(v -> {
            try {
                return Arrays.asList(objectMapper.readValue(v, String[].class));
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, APP_INFO);
    }

    public List<String> setApplicationInfo(final List<String> applicationInfo) {
        propertyUtilService.setProperty(data -> {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, APP_INFO, applicationInfo);
        return applicationInfo;
    }

    public CompanyInfoData getCompanyInfo() {
        return propertyUtilService.getProperty(v -> {
            try {
                return objectMapper.readValue(v, CompanyInfoData.class);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, COMPANY_INFO);
    }

    public CompanyInfoData setCompanyInfo(final CompanyInfoData companyInfo) {
        propertyUtilService.setProperty(data -> {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, COMPANY_INFO, companyInfo);
        return companyInfo;
    }

    public List<UnitData> getUnits() {
        return propertyUtilService.getProperty(v -> {
            try {
                return Arrays.asList(objectMapper.readValue(v, UnitData[].class));
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, UNITS);
    }

    public List<UnitData> setUnits(final List<UnitData> units) {
        propertyUtilService.setProperty(data -> {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, UNITS, units);
        return units;
    }

    public BigDecimal getVatRate() {
        return propertyUtilService.getProperty(BigDecimal::new, VAT_RATE);
    }

    public BigDecimal setVatRate(final BigDecimal value) {
        propertyUtilService.setProperty(BigDecimal::toPlainString, VAT_RATE, value);
        return value;
    }

    public String getBusinessConditions() {
        return propertyUtilService.getProperty(BUSINESS_CONDITIONS);
    }

    public String setBusinessConditions(final String businessConditions) {
        propertyUtilService.setProperty(BUSINESS_CONDITIONS, businessConditions);
        return businessConditions;
    }

    public String getCookiesInfo() {
        return propertyUtilService.getProperty(COOKIES_INFO);
    }

    public String setCookiesInfo(final String cookiesInfo) {
        propertyUtilService.setProperty(COOKIES_INFO, cookiesInfo);
        return cookiesInfo;
    }

    public String getGdprInfo() {
        return propertyUtilService.getProperty(GDPR_INFO);
    }

    public String setGdprInfo(final String gdprInfo) {
        propertyUtilService.setProperty(GDPR_INFO, gdprInfo);
        return gdprInfo;
    }

    public String getWorkingHours() {
        return propertyUtilService.getProperty(WORKING_HOURS);
    }

    public String setWorkingHours(final String workingHours) {
        propertyUtilService.setProperty(WORKING_HOURS, workingHours);
        return workingHours;
    }

    public SignUpMailData getSignUpMail() {
        return propertyUtilService.getProperty(v -> {
            try {
                return objectMapper.readValue(v, SignUpMailData.class);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, SIGN_UP_MAIL);
    }

    public SignUpMailData setSignUpMail(final SignUpMailData signUpMail) {
        propertyUtilService.setProperty(data -> {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, SIGN_UP_MAIL, signUpMail);
        return signUpMail;
    }

    public ResetPasswordMailData getResetPasswordMail() {
        return propertyUtilService.getProperty(v -> {
            try {
                return objectMapper.readValue(v, ResetPasswordMailData.class);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, RESET_PASSWORD_MAIL);
    }

    public ResetPasswordMailData setResetPasswordMail(final ResetPasswordMailData resetPasswordMail) {
        propertyUtilService.setProperty(data -> {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, RESET_PASSWORD_MAIL, resetPasswordMail);
        return resetPasswordMail;
    }
}
