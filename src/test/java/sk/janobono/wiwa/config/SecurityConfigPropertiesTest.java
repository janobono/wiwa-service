package sk.janobono.wiwa.config;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigPropertiesTest {

    @Test
    void fullTest() {
        final Pattern publicPathPattern = Pattern.compile(SecurityConfigProperties.DEFAULT_PUBLIC_PATH_PATTERN_REGEX);
        assertThat(publicPathPattern.matcher("/").matches()).isFalse();
        assertThat(publicPathPattern.matcher("/index.html").matches()).isFalse();
        assertThat(publicPathPattern.matcher("/something").matches()).isFalse();

        assertThat(publicPathPattern.matcher("/actuator/health").matches()).isTrue();
        assertThat(publicPathPattern.matcher("/actuator/info").matches()).isTrue();
        assertThat(publicPathPattern.matcher("/actuator/metrics").matches()).isTrue();
        assertThat(publicPathPattern.matcher("/actuator/metrics/something").matches()).isTrue();
        assertThat(publicPathPattern.matcher("/actuator/metrics-something").matches()).isFalse();

        assertThat(publicPathPattern.matcher("/api-docs").matches()).isTrue();
        assertThat(publicPathPattern.matcher("/api-docs.yaml").matches()).isTrue();
        assertThat(publicPathPattern.matcher("/api-docsanything").matches()).isTrue();
        assertThat(publicPathPattern.matcher("/api-docs.anything").matches()).isTrue();

        assertThat(publicPathPattern.matcher("/swagger-ui").matches()).isTrue();
        assertThat(publicPathPattern.matcher("/swagger-ui.html").matches()).isTrue();
        assertThat(publicPathPattern.matcher("/swagger-uianything").matches()).isTrue();
        assertThat(publicPathPattern.matcher("/swagger-ui.anything").matches()).isTrue();

        assertThat(publicPathPattern.matcher("/auth/change-email").matches()).isFalse();
        assertThat(publicPathPattern.matcher("/auth/change-password").matches()).isFalse();
        assertThat(publicPathPattern.matcher("/auth/change-user-details").matches()).isFalse();
        assertThat(publicPathPattern.matcher("/auth/resend-confirmation").matches()).isFalse();
        assertThat(publicPathPattern.matcher("/auth/confirm").matches()).isTrue();
        assertThat(publicPathPattern.matcher("/auth/reset-password").matches()).isTrue();
        assertThat(publicPathPattern.matcher("/auth/sign-in").matches()).isTrue();
        assertThat(publicPathPattern.matcher("/auth/sign-up").matches()).isTrue();
        assertThat(publicPathPattern.matcher("/auth/refresh").matches()).isTrue();

        assertThat(publicPathPattern.matcher("/captcha").matches()).isTrue();

        assertThat(publicPathPattern.matcher("/ui/something").matches()).isTrue();
        assertThat(publicPathPattern.matcher("/ui-something").matches()).isFalse();
    }
}
