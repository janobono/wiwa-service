package sk.janobono.wiwa.dal.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.domain.OrderMaterialDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.model.OrderMaterialIdDo;
import sk.janobono.wiwa.dal.repository.OrderMaterialRepository;
import sk.janobono.wiwa.dal.repository.OrderRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMaterialRepositoryTest extends BaseRepositoryTest {

    @Autowired
    public OrderMaterialRepository orderMaterialRepository;

    @Autowired
    private OrderRepository orderRepository;

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

        final OrderDo order = orderRepository.insert(OrderDo.builder()
                .userId(user.getId())
                .created(LocalDateTime.now())
                .orderNumber(1L)
                .weight(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .summary("")
                .build());

        assertThat(orderMaterialRepository.countById(OrderMaterialIdDo.builder()
                .orderId(order.getId())
                .materialId(1L)
                .code("code")
                .build())).isEqualTo(0);

        final OrderMaterialDo orderMaterial = orderMaterialRepository.save(OrderMaterialDo.builder()
                .orderId(order.getId())
                .materialId(1L)
                .code("code")
                .data("data")
                .build());
        orderMaterialRepository.save(orderMaterial);

        assertThat(orderMaterialRepository.countById(OrderMaterialIdDo.builder()
                .orderId(order.getId())
                .materialId(1L)
                .code("code")
                .build())).isEqualTo(1);

        Optional<OrderMaterialDo> saved = orderMaterialRepository.findById(OrderMaterialIdDo.builder()
                .orderId(order.getId())
                .materialId(1L)
                .code("code")
                .build());
        assertThat(saved.isPresent()).isTrue();

        List<OrderMaterialDo> items = orderMaterialRepository.findAllByOrderId(order.getId());
        assertThat(items).hasSize(1);
        assertThat(items.getFirst()).isEqualTo(saved.get());

        orderMaterialRepository.deleteById(OrderMaterialIdDo.builder()
                .orderId(order.getId())
                .materialId(1L)
                .code("code")
                .build());

        saved = orderMaterialRepository.findById(OrderMaterialIdDo.builder()
                .orderId(order.getId())
                .materialId(1L)
                .code("code")
                .build());
        assertThat(saved.isEmpty()).isTrue();

        items = orderMaterialRepository.findAllByOrderId(order.getId());
        assertThat(items).isEmpty();
    }
}
