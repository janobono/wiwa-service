package sk.janobono.wiwa.common.model;

import java.text.MessageFormat;

public enum WiwaProperty implements ApplicationPropertyKey {

    APP_TITLE("APP", "TITLE", true),
    APP_WELCOME_TEXT("APP", "WELCOME_TEXT", true),
    APP_COOKIES_INFO("APP", "COOKIES_INFO", true),
    APP_GDPR_INFO("APP", "GDPR_INFO", true),
    APP_WORKING_HOURS("APP", "WORKING_HOURS", true),

    APP_INFO_SLIDE_COUNT("APP_INFO_SLIDE", "COUNT", false),
    APP_INFO_SLIDE_X_TITLE("APP_INFO_SLIDE", "{0}_TITLE", true),
    APP_INFO_SLIDE_X_TEXT("APP_INFO_SLIDE", "{0}_TEXT", true),
    APP_INFO_SLIDE_X_IMAGE("APP_INFO_SLIDE", "{0}_IMAGE", false),

    COMPANY_NAME("COMPANY", "NAME", true),
    COMPANY_STREET("COMPANY", "STREET", true),
    COMPANY_ZIP_CODE("COMPANY", "ZIP_CODE", false),
    COMPANY_CITY("COMPANY", "CITY", true),
    COMPANY_STATE("COMPANY", "STATE", true),
    COMPANY_PHONE("COMPANY", "PHONE", false),
    COMPANY_MAIL("COMPANY", "MAIL", false),
    COMPANY_COMMERCIAL_REGISTER_INFO("COMPANY", "COMMERCIAL_REGISTER_INFO", true),
    COMPANY_BUSINESS_ID("COMPANY", "BUSINESS_ID", false),
    COMPANY_TAX_ID("COMPANY", "TAX_ID", false),
    COMPANY_VAT_REG_NO("COMPANY", "VAT_REG_NO", false),
    COMPANY_MAP_URL("COMPANY", "MAP_URL", false),

    AUTH_RESET_PASSWORD_MAIL_SUBJECT("AUTH_RESET_PASSWORD_MAIL", "SUBJECT", true),
    AUTH_RESET_PASSWORD_MAIL_TITLE("AUTH_RESET_PASSWORD_MAIL", "TITLE", true),
    AUTH_RESET_PASSWORD_MAIL_MESSAGE("AUTH_RESET_PASSWORD_MAIL", "MESSAGE", true),
    AUTH_RESET_PASSWORD_MAIL_PASSWORD_MESSAGE("AUTH_RESET_PASSWORD_MAIL", "PASSWORD_MESSAGE", true),
    AUTH_RESET_PASSWORD_MAIL_LINK("AUTH_RESET_PASSWORD_MAIL", "LINK", true),

    AUTH_SIGN_UP_MAIL_SUBJECT("AUTH_SIGN_UP_MAIL", "SUBJECT", true),
    AUTH_SIGN_UP_MAIL_TITLE("AUTH_SIGN_UP_MAIL", "TITLE", true),
    AUTH_SIGN_UP_MAIL_MESSAGE("AUTH_SIGN_UP_MAIL", "MESSAGE", true),
    AUTH_SIGN_UP_MAIL_LINK("AUTH_SIGN_UP_MAIL", "LINK", true);

    private final String group;
    private final String keyPattern;
    private final boolean localized;

    WiwaProperty(final String group, final String keyPattern, final boolean localized) {
        this.group = group;
        this.keyPattern = keyPattern;
        this.localized = localized;
    }

    public String getGroup() {
        return group;
    }

    public String getKey(final Object... arguments) {
        return MessageFormat.format(keyPattern, arguments);
    }

    public boolean isLocalized() {
        return localized;
    }
}
