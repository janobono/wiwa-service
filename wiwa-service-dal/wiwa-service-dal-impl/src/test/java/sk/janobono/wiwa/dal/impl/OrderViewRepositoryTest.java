package sk.janobono.wiwa.dal.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.domain.OrderStatusDo;
import sk.janobono.wiwa.dal.domain.OrderViewDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.model.OrderViewSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.OrderRepository;
import sk.janobono.wiwa.dal.repository.OrderStatusRepository;
import sk.janobono.wiwa.dal.repository.OrderViewRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;
import sk.janobono.wiwa.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class OrderViewRepositoryTest extends BaseRepositoryTest {

    @Autowired
    public OrderViewRepository orderViewRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public OrderStatusRepository orderStatusRepository;

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

        final OrderDo order = orderRepository.insert(OrderDo.builder()
                .userId(user.getId())
                .created(LocalDateTime.now())
                .orderNumber(1L)
                .weight(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .summary("")
                .build());

        orderStatusRepository.save(OrderStatusDo.builder()
                .orderId(order.getId())
                .userId(user.getId())
                .created(LocalDateTime.now())
                .status(OrderStatus.NEW)
                .build());

        orderStatusRepository.save(OrderStatusDo.builder()
                .orderId(order.getId())
                .userId(user.getId())
                .created(LocalDateTime.now())
                .status(OrderStatus.SENT)
                .build());

        Optional<OrderViewDo> saved = orderViewRepository.findById(-1L);
        assertThat(saved.isEmpty()).isTrue();

        saved = orderViewRepository.findById(order.getId());
        assertThat(saved.isPresent()).isTrue();
        assertThat(saved.get().status()).isEqualTo(OrderStatus.SENT);

        final Page<OrderViewDo> searchResult = orderViewRepository.findAll(OrderViewSearchCriteriaDo.builder().build(), Pageable.unpaged());
        assertThat(searchResult.getTotalElements()).isEqualTo(1);
        assertThat(searchResult.getTotalPages()).isEqualTo(1);
        assertThat(searchResult.getSize()).isEqualTo(1);
        assertThat(searchResult.getContent().size()).isEqualTo(1);
        assertThat(searchResult.getContent().getFirst()).isEqualTo(saved.get());
    }
}
