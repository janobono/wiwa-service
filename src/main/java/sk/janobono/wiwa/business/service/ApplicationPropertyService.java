package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.model.ui.ApplicationPropertiesData;
import sk.janobono.wiwa.business.model.ui.CompanyInfoData;
import sk.janobono.wiwa.business.model.ui.UnitData;
import sk.janobono.wiwa.business.service.util.PropertyUtilService;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.config.JwtConfigProperties;
import sk.janobono.wiwa.model.Unit;
import sk.janobono.wiwa.model.WiwaProperty;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ApplicationPropertyService {

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
        return propertyUtilService.getProperty(WiwaProperty.APP_TITLE);
    }

    public String getWelcomeText() {
        return propertyUtilService.getProperty(WiwaProperty.APP_WELCOME_TEXT);
    }

    public List<String> getApplicationInfo() {
        final List<String> result = new ArrayList<>();
        final int count = Integer.parseInt(propertyUtilService.getProperty(WiwaProperty.APP_INFO_SLIDE_COUNT));
        for (int i = 0; i < count; i++) {
            result.add(propertyUtilService.getProperty(WiwaProperty.APP_INFO_SLIDE_X_TEXT, i));
        }
        return result;
    }

    public CompanyInfoData getCompanyInfo() {
        return new CompanyInfoData(propertyUtilService.getProperty(WiwaProperty.COMPANY_NAME),
                propertyUtilService.getProperty(WiwaProperty.COMPANY_STREET),
                propertyUtilService.getProperty(WiwaProperty.COMPANY_CITY),
                propertyUtilService.getProperty(WiwaProperty.COMPANY_ZIP_CODE),
                propertyUtilService.getProperty(WiwaProperty.COMPANY_STATE),
                propertyUtilService.getProperty(WiwaProperty.COMPANY_PHONE),
                propertyUtilService.getProperty(WiwaProperty.COMPANY_MAIL),
                propertyUtilService.getProperty(WiwaProperty.COMPANY_BUSINESS_ID),
                propertyUtilService.getProperty(WiwaProperty.COMPANY_TAX_ID),
                propertyUtilService.getProperty(WiwaProperty.COMPANY_VAT_REG_NO),
                propertyUtilService.getProperty(WiwaProperty.COMPANY_COMMERCIAL_REGISTER_INFO),
                propertyUtilService.getProperty(WiwaProperty.COMPANY_MAP_URL));
    }

    public String getBusinessConditions() {
        return propertyUtilService.getProperty(WiwaProperty.APP_BUSINESS_CONDITIONS);
    }

    public String getCookiesInfo() {
        return propertyUtilService.getProperty(WiwaProperty.APP_COOKIES_INFO);
    }

    public String getGdprInfo() {
        return propertyUtilService.getProperty(WiwaProperty.APP_GDPR_INFO);
    }

    public String getWorkingHours() {
        return propertyUtilService.getProperty(WiwaProperty.APP_WORKING_HOURS);
    }

    public List<UnitData> getUnits() {
        return propertyUtilService.getProperties(data -> data.entrySet().stream().map(
                                entry -> new UnitData(Unit.valueOf(entry.getKey()), entry.getValue()))
                        .sorted(Comparator.comparingInt(o -> o.id().ordinal()))
                        .toList(),
                WiwaProperty.UNIT_GROUP.getGroup());
    }

    public BigDecimal getVatRate() {
        return propertyUtilService.getPropertyValue(BigDecimal::new, WiwaProperty.PRODUCT_VAT_RATE)
                .orElse(null);
    }

    public String setTitle(final String title) {
        propertyUtilService.setProperty(WiwaProperty.APP_TITLE.getGroup(), WiwaProperty.APP_TITLE.getKey(), title);
        return title;
    }

    public String setWelcomeText(final String welcomeText) {
        propertyUtilService.setProperty(WiwaProperty.APP_WELCOME_TEXT.getGroup(), WiwaProperty.APP_WELCOME_TEXT.getKey(), welcomeText);
        return welcomeText;
    }

    public List<String> setApplicationInfo(final List<String> applicationInfo) {
        propertyUtilService.setProperties(data -> {
            final Map<String, String> map = new HashMap<>();
            map.put(WiwaProperty.APP_INFO_SLIDE_COUNT.getKey(), Integer.toString(data.size()));
            for (int i = 0; i < data.size(); i++) {
                map.put(WiwaProperty.APP_INFO_SLIDE_X_TEXT.getKey(i), data.get(i));
            }
            return map;
        }, WiwaProperty.COMPANY_NAME.getGroup(), applicationInfo);
        return applicationInfo;
    }

    public CompanyInfoData setCompanyInfo(final CompanyInfoData companyInfo) {
        propertyUtilService.setProperties(data -> {
            final Map<String, String> map = new HashMap<>();
            map.put(WiwaProperty.COMPANY_NAME.getKey(), data.name());
            map.put(WiwaProperty.COMPANY_STREET.getKey(), data.street());
            map.put(WiwaProperty.COMPANY_CITY.getKey(), data.city());
            map.put(WiwaProperty.COMPANY_ZIP_CODE.getKey(), data.zipCode());
            map.put(WiwaProperty.COMPANY_STATE.getKey(), data.state());
            map.put(WiwaProperty.COMPANY_PHONE.getKey(), data.phone());
            map.put(WiwaProperty.COMPANY_MAIL.getKey(), data.mail());
            map.put(WiwaProperty.COMPANY_BUSINESS_ID.getKey(), data.businessId());
            map.put(WiwaProperty.COMPANY_TAX_ID.getKey(), data.taxId());
            map.put(WiwaProperty.COMPANY_VAT_REG_NO.getKey(), data.vatRegNo());
            map.put(WiwaProperty.COMPANY_COMMERCIAL_REGISTER_INFO.getKey(), data.commercialRegisterInfo());
            map.put(WiwaProperty.COMPANY_MAP_URL.getKey(), data.mapUrl());
            return map;
        }, WiwaProperty.COMPANY_NAME.getGroup(), companyInfo);
        return companyInfo;
    }

    public String setBusinessConditions(final String businessConditions) {
        propertyUtilService.setProperty(WiwaProperty.APP_BUSINESS_CONDITIONS.getGroup(), WiwaProperty.APP_BUSINESS_CONDITIONS.getKey(), businessConditions);
        return businessConditions;
    }

    public String setCookiesInfo(final String cookiesInfo) {
        propertyUtilService.setProperty(WiwaProperty.APP_COOKIES_INFO.getGroup(), WiwaProperty.APP_COOKIES_INFO.getKey(), cookiesInfo);
        return cookiesInfo;
    }

    public String setGdprInfo(final String gdprInfo) {
        propertyUtilService.setProperty(WiwaProperty.APP_GDPR_INFO.getGroup(), WiwaProperty.APP_GDPR_INFO.getKey(), gdprInfo);
        return gdprInfo;
    }

    public String setWorkingHours(final String workingHours) {
        propertyUtilService.setProperty(WiwaProperty.APP_WORKING_HOURS.getGroup(), WiwaProperty.APP_WORKING_HOURS.getKey(), workingHours);
        return workingHours;
    }

    public List<UnitData> setUnits(final List<UnitData> data) {
        propertyUtilService.setProperties(d -> d.stream()
                        .map(unit -> Map.entry(unit.id().name(), unit.value()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                , WiwaProperty.UNIT_GROUP.getGroup(), data);
        return data;
    }

    public BigDecimal setVatRate(final BigDecimal value) {
        propertyUtilService.setProperty(BigDecimal::toPlainString,
                WiwaProperty.PRODUCT_VAT_RATE.getGroup(), WiwaProperty.PRODUCT_VAT_RATE.getKey(), value);
        return value;
    }
}
