package sk.janobono.wiwa.dal.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.domain.OrderItemDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.OrderItemRepository;
import sk.janobono.wiwa.dal.repository.OrderRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemRepositoryTest extends BaseRepositoryTest {

    @Autowired
    public OrderItemRepository orderItemRepository;

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

        assertThat(orderItemRepository.countByOrderId(order.getId())).isEqualTo(0);
        Optional<OrderItemDo> saved = orderItemRepository.findById(-1L);
        assertThat(saved.isEmpty()).isTrue();
        saved = orderItemRepository.findByOrderIdAndSortNum(order.getId(), -1);
        assertThat(saved.isEmpty()).isTrue();

        final List<OrderItemDo> items = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            final OrderItemDo orderItem = orderItemRepository.insert(OrderItemDo.builder()
                    .orderId(order.getId())
                    .sortNum(i)
                    .name("item%d".formatted(i))
                    .quantity(i)
                    .part("part%d".formatted(i))
                    .build());
            saved = orderItemRepository.findById(orderItem.getId());
            assertThat(saved.isPresent()).isTrue();
            assertThat(saved.get()).usingRecursiveComparison().isEqualTo(orderItem);
            saved = orderItemRepository.findByOrderIdAndSortNum(order.getId(), orderItem.getSortNum());
            assertThat(saved.isPresent()).isTrue();
            assertThat(saved.get()).usingRecursiveComparison().isEqualTo(orderItem);
            items.add(orderItem);
        }
        assertThat(orderItemRepository.countByOrderId(order.getId())).isEqualTo(10);

        List<OrderItemDo> items2 = orderItemRepository.findAllByOrderId(order.getId());
        assertThat(items).hasSize(items2.size());
        assertThat(items.getFirst()).usingRecursiveComparison().isEqualTo(items2.getFirst());
        assertThat(items.getLast()).usingRecursiveComparison().isEqualTo(items2.getLast());

        orderItemRepository.setSortNum(items.getLast().getId(), 0);
        orderItemRepository.setSortNum(items.getFirst().getId(), 9);

        items2 = orderItemRepository.findAllByOrderId(order.getId());
        assertThat(items).hasSize(items2.size());
        assertThat(items.getFirst().getId()).isEqualTo(items2.getLast().getId());
        assertThat(items.getLast().getId()).isEqualTo(items2.getFirst().getId());

        orderItemRepository.setName(items.getFirst().getId(), "nameX");
        orderItemRepository.setDescription(items.getFirst().getId(), "descriptionX");
        orderItemRepository.setQuantity(items.getFirst().getId(), 0);
        saved = orderItemRepository.findById(items.getFirst().getId());
        assertThat(saved.isPresent()).isTrue();
        assertThat(saved.get().getName()).isEqualTo("nameX");
        assertThat(saved.get().getDescription()).isEqualTo("descriptionX");
        assertThat(saved.get().getQuantity()).isEqualTo(0);

        orderItemRepository.setPart(items.getFirst().getId(), "partX");
        saved = orderItemRepository.findById(items.getFirst().getId());
        assertThat(saved.isPresent()).isTrue();
        assertThat(saved.get().getPart()).isEqualTo("partX");

        for (final OrderItemDo item : items) {
            orderItemRepository.deleteById(item.getId());
        }
        assertThat(orderItemRepository.countByOrderId(order.getId())).isEqualTo(0);
    }
}
