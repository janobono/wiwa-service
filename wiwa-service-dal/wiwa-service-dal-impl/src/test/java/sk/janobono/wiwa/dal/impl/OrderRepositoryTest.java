package sk.janobono.wiwa.dal.impl;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.OrderRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;
import sk.janobono.wiwa.model.OrderPackageType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class OrderRepositoryTest extends BaseRepositoryTest {

    @Autowired
    public OrderRepository orderRepository;

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

        Optional<OrderDo> saved = orderRepository.findById(-1L);
        assertThat(saved.isEmpty()).isTrue();
        Optional<Long> userId = orderRepository.getOrderUserId(-1L);
        assertThat(userId.isEmpty()).isTrue();


        final List<OrderDo> orders = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            final OrderDo order = orderRepository.insert(OrderDo.builder()
                    .userId(user.getId())
                    .created(LocalDateTime.now())
                    .orderNumber((long) i)
                    .weight(BigDecimal.valueOf(i).setScale(3, RoundingMode.HALF_UP))
                    .total(BigDecimal.valueOf(i).setScale(3, RoundingMode.HALF_UP))
                    .summary("summary%d".formatted(i))
                    .build());
            saved = orderRepository.findById(order.getId());
            assertThat(saved.isPresent()).isTrue();
            assertThat(saved.get()).usingRecursiveComparison(
                    RecursiveComparisonConfiguration.builder().withIgnoredFields("created").build()
            ).isEqualTo(order);
            userId = orderRepository.getOrderUserId(order.getId());
            assertThat(userId.isPresent()).isTrue();
            assertThat(userId.get()).isEqualTo(order.getUserId());
            orders.add(order);
        }

        assertThat(orders.getFirst().getDelivery()).isNull();
        assertThat(orders.getFirst().getPackageType()).isNull();
        orderRepository.setContact(orders.getFirst().getId(), "contact");
        orderRepository.setDelivery(orders.getFirst().getId(), LocalDate.now());
        orderRepository.setPackageType(orders.getFirst().getId(), OrderPackageType.PACKAGE);
        saved = orderRepository.findById(orders.getFirst().getId());
        assertThat(saved.isPresent()).isTrue();
        assertThat(saved.get().getContact()).isEqualTo("contact");
        assertThat(saved.get().getDelivery()).isNotNull();
        assertThat(saved.get().getPackageType()).isEqualTo(OrderPackageType.PACKAGE);

        orderRepository.setWeight(orders.getFirst().getId(), BigDecimal.valueOf(10.001));
        orderRepository.setTotal(orders.getFirst().getId(), BigDecimal.valueOf(10.002));
        saved = orderRepository.findById(orders.getFirst().getId());
        assertThat(saved.isPresent()).isTrue();
        assertThat(saved.get().getWeight()).isEqualTo(BigDecimal.valueOf(10.001));
        assertThat(saved.get().getTotal()).isEqualTo(BigDecimal.valueOf(10.002));

        orderRepository.setSummary(orders.getFirst().getId(), "summaryX");
        saved = orderRepository.findById(orders.getFirst().getId());
        assertThat(saved.isPresent()).isTrue();
        assertThat(saved.get().getSummary()).isEqualTo("summaryX");

        for (final OrderDo order : orders) {
            orderRepository.deleteById(order.getId());
            saved = orderRepository.findById(order.getId());
            assertThat(saved.isEmpty()).isTrue();
        }
    }
}
