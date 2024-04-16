package sk.janobono.wiwa.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Authority {

    W_ADMIN("w-admin"),
    W_MANAGER("w-manager"),
    W_EMPLOYEE("w-employee"),
    W_CUSTOMER("w-customer");

    private final String value;

    Authority(final String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }

    public static Authority byValue(final String value) {
        Authority result = null;
        for (final Authority authority : Authority.values()) {
            if (authority.value.equals(value)) {
                result = authority;
                break;
            }
        }
        return result;
    }
}
