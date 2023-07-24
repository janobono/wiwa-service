package sk.janobono.wiwa.model;

import java.text.MessageFormat;

public enum WiwaProperty implements ApplicationPropertyKey {

    APP_TITLE("APP", "TITLE"),
    APP_WELCOME_TEXT("APP", "WELCOME_TEXT"),
    APP_COOKIES_INFO("APP", "COOKIES_INFO"),
    APP_GDPR_INFO("APP", "GDPR_INFO"),
    APP_WORKING_HOURS("APP", "WORKING_HOURS"),

    APP_INFO_SLIDE_COUNT("APP_INFO_SLIDE", "COUNT"),
    APP_INFO_SLIDE_X_TITLE("APP_INFO_SLIDE", "{0}_TITLE"),
    APP_INFO_SLIDE_X_TEXT("APP_INFO_SLIDE", "{0}_TEXT"),
    APP_INFO_SLIDE_X_IMAGE("APP_INFO_SLIDE", "{0}_IMAGE"),

    COMPANY_NAME("COMPANY", "NAME"),
    COMPANY_STREET("COMPANY", "STREET"),
    COMPANY_ZIP_CODE("COMPANY", "ZIP_CODE"),
    COMPANY_CITY("COMPANY", "CITY"),
    COMPANY_STATE("COMPANY", "STATE"),
    COMPANY_PHONE("COMPANY", "PHONE"),
    COMPANY_MAIL("COMPANY", "MAIL"),
    COMPANY_COMMERCIAL_REGISTER_INFO("COMPANY", "COMMERCIAL_REGISTER_INFO"),
    COMPANY_BUSINESS_ID("COMPANY", "BUSINESS_ID"),
    COMPANY_TAX_ID("COMPANY", "TAX_ID"),
    COMPANY_VAT_REG_NO("COMPANY", "VAT_REG_NO"),
    COMPANY_MAP_URL("COMPANY", "MAP_URL"),

    AUTH_RESET_PASSWORD_MAIL_SUBJECT("AUTH_RESET_PASSWORD_MAIL", "SUBJECT"),
    AUTH_RESET_PASSWORD_MAIL_TITLE("AUTH_RESET_PASSWORD_MAIL", "TITLE"),
    AUTH_RESET_PASSWORD_MAIL_MESSAGE("AUTH_RESET_PASSWORD_MAIL", "MESSAGE"),
    AUTH_RESET_PASSWORD_MAIL_PASSWORD_MESSAGE("AUTH_RESET_PASSWORD_MAIL", "PASSWORD_MESSAGE"),
    AUTH_RESET_PASSWORD_MAIL_LINK("AUTH_RESET_PASSWORD_MAIL", "LINK"),

    AUTH_SIGN_UP_MAIL_SUBJECT("AUTH_SIGN_UP_MAIL", "SUBJECT"),
    AUTH_SIGN_UP_MAIL_TITLE("AUTH_SIGN_UP_MAIL", "TITLE"),
    AUTH_SIGN_UP_MAIL_MESSAGE("AUTH_SIGN_UP_MAIL", "MESSAGE"),
    AUTH_SIGN_UP_MAIL_LINK("AUTH_SIGN_UP_MAIL", "LINK");

    private final String group;
    private final String keyPattern;

    WiwaProperty(final String group, final String keyPattern) {
        this.group = group;
        this.keyPattern = keyPattern;
    }

    public String getGroup() {
        return group;
    }

    public String getKey(final Object... arguments) {
        return MessageFormat.format(keyPattern, arguments);
    }
}