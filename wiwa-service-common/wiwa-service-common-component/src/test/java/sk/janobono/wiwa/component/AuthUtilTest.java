package sk.janobono.wiwa.component;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sk.janobono.wiwa.model.Authority;
import sk.janobono.wiwa.model.User;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {AuthUtil.class}
)
class AuthUtilTest {

    private static final User CUSTOMER = User.builder().authorities(List.of(Authority.W_CUSTOMER)).build();
    private static final User EMPLOYEE = User.builder().authorities(List.of(Authority.W_EMPLOYEE)).build();
    private static final User MANAGER = User.builder().authorities(List.of(Authority.W_MANAGER)).build();
    private static final User ADMIN = User.builder().authorities(List.of(Authority.W_ADMIN)).build();
    private static final User ALL = User.builder().authorities(Arrays.asList(Authority.values())).build();

    @Autowired
    public AuthUtil authUtil;

    @Test
    void fullTest() {
        assertThat(authUtil.hasAnyAuthority(CUSTOMER)).isEqualTo(false);
        assertThat(authUtil.hasAnyAuthority(EMPLOYEE)).isEqualTo(false);
        assertThat(authUtil.hasAnyAuthority(MANAGER)).isEqualTo(false);
        assertThat(authUtil.hasAnyAuthority(ADMIN)).isEqualTo(false);
        assertThat(authUtil.hasAnyAuthority(ALL)).isEqualTo(false);

        assertThat(authUtil.hasAnyAuthority(CUSTOMER, Authority.values())).isEqualTo(true);
        assertThat(authUtil.hasAnyAuthority(EMPLOYEE, Authority.values())).isEqualTo(true);
        assertThat(authUtil.hasAnyAuthority(MANAGER, Authority.values())).isEqualTo(true);
        assertThat(authUtil.hasAnyAuthority(ADMIN, Authority.values())).isEqualTo(true);
        assertThat(authUtil.hasAnyAuthority(ALL, Authority.values())).isEqualTo(true);

        final var NO_CUSTOMER = new Authority[]{Authority.W_EMPLOYEE, Authority.W_MANAGER, Authority.W_ADMIN};

        assertThat(authUtil.hasAnyAuthority(CUSTOMER, NO_CUSTOMER)).isEqualTo(false);
        assertThat(authUtil.hasAnyAuthority(EMPLOYEE, NO_CUSTOMER)).isEqualTo(true);
        assertThat(authUtil.hasAnyAuthority(MANAGER, NO_CUSTOMER)).isEqualTo(true);
        assertThat(authUtil.hasAnyAuthority(ADMIN, NO_CUSTOMER)).isEqualTo(true);
        assertThat(authUtil.hasAnyAuthority(ALL, NO_CUSTOMER)).isEqualTo(true);

        final var NO_CUSTOMER_EMPLOYEE = new Authority[]{Authority.W_MANAGER, Authority.W_ADMIN};

        assertThat(authUtil.hasAnyAuthority(CUSTOMER, NO_CUSTOMER_EMPLOYEE)).isEqualTo(false);
        assertThat(authUtil.hasAnyAuthority(EMPLOYEE, NO_CUSTOMER_EMPLOYEE)).isEqualTo(false);
        assertThat(authUtil.hasAnyAuthority(MANAGER, NO_CUSTOMER_EMPLOYEE)).isEqualTo(true);
        assertThat(authUtil.hasAnyAuthority(ADMIN, NO_CUSTOMER_EMPLOYEE)).isEqualTo(true);
        assertThat(authUtil.hasAnyAuthority(ALL, NO_CUSTOMER_EMPLOYEE)).isEqualTo(true);

        final var JUST_ADMIN = new Authority[]{Authority.W_ADMIN};

        assertThat(authUtil.hasAnyAuthority(CUSTOMER, JUST_ADMIN)).isEqualTo(false);
        assertThat(authUtil.hasAnyAuthority(EMPLOYEE, JUST_ADMIN)).isEqualTo(false);
        assertThat(authUtil.hasAnyAuthority(MANAGER, JUST_ADMIN)).isEqualTo(false);
        assertThat(authUtil.hasAnyAuthority(ADMIN, JUST_ADMIN)).isEqualTo(true);
        assertThat(authUtil.hasAnyAuthority(ALL, JUST_ADMIN)).isEqualTo(true);
    }
}
