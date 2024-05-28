package sk.janobono.wiwa.dal.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sk.janobono.wiwa.dal.domain.OrderCommentDo;
import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.OrderCommentRepository;
import sk.janobono.wiwa.dal.repository.OrderRepository;
import sk.janobono.wiwa.dal.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderCommentRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private OrderCommentRepository orderCommentRepository;

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

        List<OrderCommentDo> comments = orderCommentRepository.findAllByOrderId(order.getId());
        assertThat(comments).hasSize(0);

        final OrderCommentDo comment = orderCommentRepository.save(OrderCommentDo.builder()
                .orderId(order.getId())
                .userId(user.getId())
                .created(LocalDateTime.now())
                .comment("comment")
                .build());

        comments = orderCommentRepository.findAllByOrderId(order.getId());
        assertThat(comments).hasSize(1);
        assertThat(comment.getId()).isEqualTo(comments.getFirst().getId());
        assertThat(comment.getOrderId()).isEqualTo(comments.getFirst().getOrderId());
        assertThat(comment.getUserId()).isEqualTo(comments.getFirst().getUserId());
        assertThat(comment.getComment()).isEqualTo(comments.getFirst().getComment());
    }
}
