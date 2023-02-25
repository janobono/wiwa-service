package sk.janobono.wiwa.business.model.auth;

public record AuthenticationResponseSo(String token, String type, String refreshToken) {
    public AuthenticationResponseSo(String token, String refreshToken) {
        this(token, "Bearer", refreshToken);
    }
}
