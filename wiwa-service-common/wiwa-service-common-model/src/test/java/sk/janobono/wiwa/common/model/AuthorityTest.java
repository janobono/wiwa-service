package sk.janobono.wiwa.common.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorityTest {

    @Test
    void authorityByValue() {
        for (Authority authority : Authority.values()) {
            Authority parsed = Authority.byValue(authority.toString());
            assertThat(parsed).isEqualTo(authority);
        }
    }
}
