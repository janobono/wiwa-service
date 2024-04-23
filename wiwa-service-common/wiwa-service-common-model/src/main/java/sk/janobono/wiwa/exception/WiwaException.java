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
    CODE_IS_USED,
    BOARD_NOT_FOUND,
    EDGE_NOT_FOUND,
    CODE_LIST_NOT_FOUND,
    CODE_LIST_ITEM_NOT_FOUND,
    CODE_LIST_ITEM_NOT_EMPTY,
    QUANTITY_UNIT_NOT_FOUND,
    ORDER_NOT_FOUND,
    ORDER_IS_IMMUTABLE,
    ORDER_STATUS_INVALID,
    ORDER_IS_EMPTY,
    ORDER_AGREEMENTS_INVALID,
    ORDER_DELIVERY_DATE_INVALID;

    public ApplicationException exception(final String pattern, final Object... arguments) {
        return exception(null, pattern, arguments);
    }

    public ApplicationException exception(final Throwable cause, final String pattern, final Object... arguments) {
        return new ApplicationException(this.name(), MessageFormat.format(pattern, arguments), cause);
    }
}
