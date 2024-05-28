package sk.janobono.wiwa.dal.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sk.janobono.wiwa.dal.domain.*;
import sk.janobono.wiwa.dal.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderSummaryViewRepositoryTest extends BaseRepositoryTest {

    @Autowired
    public OrderSummaryViewRepository orderSummaryViewRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    public OrderItemSummaryRepository orderItemSummaryRepository;

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

        final OrderItemDo orderItem1 = orderItemRepository.insert(OrderItemDo.builder()
                .orderId(order.getId())
                .sortNum(1)
                .name("item1")
                .quantity(1)
                .part("")
                .build());
        final OrderItemDo orderItem2 = orderItemRepository.insert(OrderItemDo.builder()
                .orderId(order.getId())
                .sortNum(2)
                .name("item2")
                .quantity(1)
                .part("")
                .build());


        orderItemSummaryRepository.insert(OrderItemSummaryDo.builder()
                .orderItemId(orderItem1.getId())
                .code("TOTAL::code1")
                .amount(BigDecimal.ONE)
                .build());
        orderItemSummaryRepository.insert(OrderItemSummaryDo.builder()
                .orderItemId(orderItem1.getId())
                .code("TOTAL::code2")
                .amount(BigDecimal.TWO)
                .build());
        orderItemSummaryRepository.insert(OrderItemSummaryDo.builder()
                .orderItemId(orderItem1.getId())
                .code("TOTAL::code3")
                .amount(BigDecimal.TEN)
                .build());

        orderItemSummaryRepository.insert(OrderItemSummaryDo.builder()
                .orderItemId(orderItem2.getId())
                .code("TOTAL::code1")
                .amount(BigDecimal.ONE)
                .build());
        orderItemSummaryRepository.insert(OrderItemSummaryDo.builder()
                .orderItemId(orderItem2.getId())
                .code("TOTAL::code2")
                .amount(BigDecimal.TWO)
                .build());
        orderItemSummaryRepository.insert(OrderItemSummaryDo.builder()
                .orderItemId(orderItem2.getId())
                .code("TOTAL::code3")
                .amount(BigDecimal.TEN)
                .build());

        final List<OrderSummaryViewDo> summary = orderSummaryViewRepository.findAllById(order.getId());
        assertThat(summary).hasSize(3);
        assertThat(summary.getFirst().amount()).isEqualTo(BigDecimal.valueOf(2).setScale(3, RoundingMode.HALF_UP));
        assertThat(summary.get(1).amount()).isEqualTo(BigDecimal.valueOf(4).setScale(3, RoundingMode.HALF_UP));
        assertThat(summary.getLast().amount()).isEqualTo(BigDecimal.valueOf(20).setScale(3, RoundingMode.HALF_UP));
    }
}
