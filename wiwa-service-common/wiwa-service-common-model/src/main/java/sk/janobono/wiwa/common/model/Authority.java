package sk.janobono.wiwa.common.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Authority {

    W_ADMIN("w-admin"),
    W_MANAGER("w-manager"),
    W_EMPLOYEE("w-employee"),
    W_CUSTOMER("w-customer");

    private final String value;

    Authority(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }

    public static Authority byValue(String value) {
        Authority result = null;
        for (Authority auth : Authority.values()) {
            if (auth.value.equals(value)) {
                result = auth;
                break;
            }
        }
        return result;
    }
}
