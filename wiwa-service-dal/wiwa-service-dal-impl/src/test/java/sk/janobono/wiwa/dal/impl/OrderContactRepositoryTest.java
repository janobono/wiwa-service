package sk.janobono.wiwa.dal.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import sk.janobono.wiwa.dal.domain.OrderContactDo;
import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.model.BaseOrderContactDo;
import sk.janobono.wiwa.dal.repository.OrderContactRepository;
import sk.janobono.wiwa.dal.repository.OrderRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class OrderContactRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private OrderContactRepository orderContactRepository;

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

        final OrderDo order1 = orderRepository.insert(OrderDo.builder()
                .userId(user.getId())
                .created(LocalDateTime.now())
                .orderNumber(1L)
                .weight(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .summary("")
                .build());
        final OrderDo order2 = orderRepository.insert(OrderDo.builder()
                .userId(user.getId())
                .created(LocalDateTime.now())
                .orderNumber(2L)
                .weight(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .summary("")
                .build());

        OrderContactDo orderContact1 = orderContactRepository.save(OrderContactDo.builder()
                .orderId(order1.getId())
                .name("name1")
                .street("street1")
                .zipCode("zipCode1")
                .city("city1")
                .state("state1")
                .phone("phone1")
                .email("email1")
                .businessId("bi1")
                .taxId("ti1")
                .build());
        orderContact1 = orderContactRepository.save(orderContact1);

        OrderContactDo orderContact2 = orderContactRepository.save(OrderContactDo.builder()
                .orderId(order2.getId())
                .name("name2")
                .street("street2")
                .zipCode("zipCode2")
                .city("city2")
                .state("state2")
                .phone("phone2")
                .email("email2")
                .build());
        orderContact2 = orderContactRepository.save(orderContact2);

        Optional<OrderContactDo> saved = orderContactRepository.findByOrderId(-1L);
        assertThat(saved.isEmpty()).isTrue();

        saved = orderContactRepository.findByOrderId(order1.getId());
        assertThat(saved.isPresent()).isTrue();
        assertThat(saved.get()).usingRecursiveComparison().isEqualTo(orderContact1);

        saved = orderContactRepository.findByOrderId(order2.getId());
        assertThat(saved.isPresent()).isTrue();
        assertThat(saved.get()).usingRecursiveComparison().isEqualTo(orderContact2);

        final Page<BaseOrderContactDo> searchAllResult = orderContactRepository.findAllByUserId(user.getId(), Pageable.unpaged());
        assertThat(searchAllResult.getTotalElements()).isEqualTo(2);
        assertThat(searchAllResult.getTotalPages()).isEqualTo(1);
        assertThat(searchAllResult.getSize()).isEqualTo(2);
        assertThat(searchAllResult.getContent().size()).isEqualTo(2);

        Page<BaseOrderContactDo> searchResult = orderContactRepository.findAllByUserId(user.getId(), PageRequest.of(0, 1, Sort.Direction.ASC, "anything"));
        assertThat(searchResult.getTotalElements()).isEqualTo(2);
        assertThat(searchResult.getTotalPages()).isEqualTo(2);
        assertThat(searchResult.getSize()).isEqualTo(1);
        assertThat(searchResult.getContent().size()).isEqualTo(1);
        assertThat(searchResult.getContent().getFirst()).isEqualTo(searchAllResult.getContent().getFirst());

        searchResult = orderContactRepository.findAllByUserId(user.getId(), PageRequest.of(0, 1, Sort.Direction.DESC, "name"));
        assertThat(searchResult.getTotalElements()).isEqualTo(2);
        assertThat(searchResult.getTotalPages()).isEqualTo(2);
        assertThat(searchResult.getSize()).isEqualTo(1);
        assertThat(searchResult.getContent().size()).isEqualTo(1);
        assertThat(searchResult.getContent().getFirst()).isEqualTo(searchAllResult.getContent().getLast());
    }
}
