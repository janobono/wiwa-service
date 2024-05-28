package sk.janobono.wiwa.dal.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.OrderNumberRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

class OrderNumberRepositoryTest extends BaseRepositoryTest {

    @Autowired
    public OrderNumberRepository orderNumberRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void fullTest() {
        final UserDo user = userRepository.save(UserDo.builder()
                .username("username")
                .password("password")
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .gdpr(false)
                .confirmed(false)
                .enabled(false)
                .build());

        assertThat(orderNumberRepository.getNextOrderNumber(user.getId())).isEqualTo(1L);
        assertThat(orderNumberRepository.getNextOrderNumber(user.getId())).isEqualTo(2L);
        assertThat(orderNumberRepository.getNextOrderNumber(user.getId())).isEqualTo(3L);
    }
}
