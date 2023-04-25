package sk.janobono.wiwa.dal.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaUserDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserDoMapperImpl.class})
class UserMapperTest {

    @Autowired
    public UserDoMapper userDoMapper;

    @Test
    void mapUser() {
        final UserDo userDo = new UserDo(
                1L,
                "username",
                "password",
                "title_before",
                "first_name",
                "mid_name",
                "last_name",
                "title_after",
                "email",
                true,
                true,
                true,
                Set.of()
        );

        final WiwaUserDto wiwaUserDto = userDoMapper.mapToDto(userDo);
        assertThat(wiwaUserDto.id()).isEqualTo(userDo.id());
        assertThat(wiwaUserDto.username()).isEqualTo(userDo.username());
        assertThat(wiwaUserDto.titleBefore()).isEqualTo(userDo.titleBefore());
        assertThat(wiwaUserDto.firstName()).isEqualTo(userDo.firstName());
        assertThat(wiwaUserDto.midName()).isEqualTo(userDo.midName());
        assertThat(wiwaUserDto.lastName()).isEqualTo(userDo.lastName());
        assertThat(wiwaUserDto.titleAfter()).isEqualTo(userDo.titleAfter());
        assertThat(wiwaUserDto.email()).isEqualTo(userDo.email());
        assertThat(wiwaUserDto.gdpr()).isTrue();
        assertThat(wiwaUserDto.confirmed()).isTrue();
        assertThat(wiwaUserDto.enabled()).isTrue();
    }
}
