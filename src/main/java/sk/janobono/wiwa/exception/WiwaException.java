package sk.janobono.wiwa.exception;

import java.text.MessageFormat;

public enum WiwaException {

    APPLICATION_IMAGE_NOT_SUPPORTED,
    APPLICATION_PROPERTY_NOT_FOUND,
    AUTHORITY_NOT_FOUND,
    GDPR,
    INVALID_CAPTCHA,
    INVALID_CREDENTIALS,
    UNSUPPORTED_VALIDATION_TOKEN,
    USER_EMAIL_IS_USED,
    USER_IS_DISABLED,
    USER_NOT_FOUND,
    USER_USERNAME_IS_USED,
    PRODUCT_NOT_FOUND,
    PRODUCT_CATEGORY_NOT_FOUND,
    PRODUCT_CATEGORY_NOT_EMPTY;

    public ApplicationException exception(final String pattern, final Object... arguments) {
        return exception(null, pattern, arguments);
    }

    public ApplicationException exception(final Throwable cause, final String pattern, final Object... arguments) {
        return new ApplicationException(this.name(), MessageFormat.format(pattern, arguments), cause);
    }
}
