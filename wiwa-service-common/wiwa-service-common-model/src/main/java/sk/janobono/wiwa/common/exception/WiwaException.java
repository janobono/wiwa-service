package sk.janobono.wiwa.common.exception;

import sk.janobono.wiwa.common.exception.ApplicationException;

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
    USER_USERNAME_IS_USED;

    public ApplicationException exception(String pattern, Object... arguments) {
        return exception(null, pattern, arguments);
    }

    public ApplicationException exception(Throwable cause, String pattern, Object... arguments) {
        return new ApplicationException(this.name(), MessageFormat.format(pattern, arguments), cause);
    }
}
