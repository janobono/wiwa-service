package sk.janobono.wiwa.api.model.auth;

public record AuthenticationResponseWebDto(String token, String type, String refreshToken) {
    public AuthenticationResponseWebDto(final String token, final String refreshToken) {
        this(token, "Bearer", refreshToken);
    }
}
