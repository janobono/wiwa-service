package sk.janobono.wiwa.business;

import org.mockito.Mockito;
import sk.janobono.wiwa.config.AuthConfigProperties;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.config.JwtConfigProperties;
import sk.janobono.wiwa.config.VerificationConfigProperties;
import sk.janobono.wiwa.model.Currency;

public class TestConfigProperties {

    public static final String DEFAULT_LOCALE = "en_US";
    public static final String APP_TITLE = "app title";
    public static final String APP_DESCRIPTION = "app description";
    public static final int EXPIRES_IN = 1;
    public static final Currency CURRENCY = new Currency("EUR", "€");

    public void mock(final AuthConfigProperties authConfigProperties) {
        Mockito.when(authConfigProperties.signUpTokenExpiration()).thenReturn(EXPIRES_IN);
        Mockito.when(authConfigProperties.resetPasswordTokenExpiration()).thenReturn(EXPIRES_IN);
        Mockito.when(authConfigProperties.refreshTokenExpiration()).thenReturn(EXPIRES_IN);
    }

    public void mock(final CommonConfigProperties commonConfigProperties) {
        Mockito.when(commonConfigProperties.defaultLocale()).thenReturn(DEFAULT_LOCALE);
        Mockito.when(commonConfigProperties.appTitle()).thenReturn(APP_TITLE);
        Mockito.when(commonConfigProperties.appDescription()).thenReturn(APP_DESCRIPTION);
        Mockito.when(commonConfigProperties.webUrl()).thenReturn("http://localhost:8080");
        Mockito.when(commonConfigProperties.confirmPath()).thenReturn("/ui/confirm/");
        Mockito.when(commonConfigProperties.ordersPath()).thenReturn("/fixme/");
        Mockito.when(commonConfigProperties.mail()).thenReturn("mail@wiwa.sk");
        Mockito.when(commonConfigProperties.ordersMail()).thenReturn("mail@wiwa.sk");
        Mockito.when(commonConfigProperties.maxImageResolution()).thenReturn(1000);
        Mockito.when(commonConfigProperties.maxThumbnailResolution()).thenReturn(130);
        Mockito.when(commonConfigProperties.captchaLength()).thenReturn(4);
        Mockito.when(commonConfigProperties.currency()).thenReturn(CURRENCY);
    }

    public void mock(final JwtConfigProperties jwtConfigProperties) {
        Mockito.when(jwtConfigProperties.issuer()).thenReturn("issuer");
        Mockito.when(jwtConfigProperties.expiration()).thenReturn(EXPIRES_IN);
    }

    public void mock(final VerificationConfigProperties verificationConfigProperties) {
        Mockito.when(verificationConfigProperties.issuer()).thenReturn("verificationIssuer");
    }
}
