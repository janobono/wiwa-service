package sk.janobono.wiwa.dal.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.domain.OrderItemDo;
import sk.janobono.wiwa.dal.domain.OrderItemSummaryDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.OrderItemRepository;
import sk.janobono.wiwa.dal.repository.OrderItemSummaryRepository;
import sk.janobono.wiwa.dal.repository.OrderRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemSummaryRepositoryTest extends BaseRepositoryTest {

    @Autowired
    public OrderItemSummaryRepository orderItemSummaryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

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

        final OrderItemDo orderItem = orderItemRepository.insert(OrderItemDo.builder()
                .orderId(order.getId())
                .sortNum(1)
                .name("item1")
                .quantity(1)
                .part("")
                .build());


        orderItemSummaryRepository.insert(OrderItemSummaryDo.builder()
                .orderItemId(orderItem.getId())
                .code("code1")
                .amount(BigDecimal.ZERO)
                .build());

        orderItemSummaryRepository.insert(OrderItemSummaryDo.builder()
                .orderItemId(orderItem.getId())
                .code("code2")
                .amount(BigDecimal.ZERO)
                .build());

        orderItemSummaryRepository.insert(OrderItemSummaryDo.builder()
                .orderItemId(orderItem.getId())
                .code("code3")
                .amount(BigDecimal.ZERO)
                .build());

        List<OrderItemSummaryDo> items = orderItemSummaryRepository.findAllByOrderItemId(orderItem.getId());
        assertThat(items).hasSize(3);

        orderItemSummaryRepository.deleteByOrderItemId(orderItem.getId());

        items = orderItemSummaryRepository.findAllByOrderItemId(orderItem.getId());
        assertThat(items).isEmpty();
    }
}
