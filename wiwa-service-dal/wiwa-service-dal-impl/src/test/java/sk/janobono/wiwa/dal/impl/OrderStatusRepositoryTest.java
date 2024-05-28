package sk.janobono.wiwa.dal.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.domain.OrderStatusDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.OrderRepository;
import sk.janobono.wiwa.dal.repository.OrderStatusRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;
import sk.janobono.wiwa.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusRepositoryTest extends BaseRepositoryTest {

    @Autowired
    public OrderStatusRepository orderStatusRepository;

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

        OrderStatusDo orderStatus1 = orderStatusRepository.save(OrderStatusDo.builder()
                .orderId(order.getId())
                .userId(user.getId())
                .created(LocalDateTime.now())
                .status(OrderStatus.NEW)
                .build());
        orderStatus1 = orderStatusRepository.save(orderStatus1);

        final OrderStatusDo orderStatus2 = orderStatusRepository.save(OrderStatusDo.builder()
                .orderId(order.getId())
                .userId(user.getId())
                .created(LocalDateTime.now())
                .status(OrderStatus.SENT)
                .build());
        orderStatusRepository.save(orderStatus2);

        final OrderStatusDo orderStatus3 = orderStatusRepository.save(OrderStatusDo.builder()
                .orderId(order.getId())
                .userId(user.getId())
                .created(LocalDateTime.now())
                .status(OrderStatus.IN_PRODUCTION)
                .build());
        orderStatusRepository.save(orderStatus3);

        final OrderStatusDo orderStatus4 = orderStatusRepository.save(OrderStatusDo.builder()
                .orderId(order.getId())
                .userId(user.getId())
                .created(LocalDateTime.now())
                .status(OrderStatus.READY)
                .build());
        orderStatusRepository.save(orderStatus4);

        final OrderStatusDo orderStatus5 = orderStatusRepository.save(OrderStatusDo.builder()
                .orderId(order.getId())
                .userId(user.getId())
                .created(LocalDateTime.now())
                .status(OrderStatus.FINISHED)
                .build());
        orderStatusRepository.save(orderStatus5);

        OrderStatusDo orderStatus6 = orderStatusRepository.save(OrderStatusDo.builder()
                .orderId(order.getId())
                .userId(user.getId())
                .created(LocalDateTime.now())
                .status(OrderStatus.CANCELLED)
                .build());
        orderStatus6 = orderStatusRepository.save(orderStatus6);

        final List<OrderStatusDo> orderStatuses = orderStatusRepository.findAllByOrderId(order.getId());
        assertThat(orderStatuses).hasSize(6);
        assertThat(orderStatuses.getFirst().getStatus()).isEqualTo(orderStatus1.getStatus());
        assertThat(orderStatuses.getLast().getStatus()).isEqualTo(orderStatus6.getStatus());
    }
}
