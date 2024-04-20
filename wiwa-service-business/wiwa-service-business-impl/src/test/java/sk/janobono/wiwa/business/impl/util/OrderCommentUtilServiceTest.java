package sk.janobono.wiwa.business.impl.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.janobono.wiwa.business.model.order.OrderCommentData;
import sk.janobono.wiwa.business.model.order.OrderUserData;
import sk.janobono.wiwa.dal.domain.OrderAttributeDo;
import sk.janobono.wiwa.dal.domain.UserDo;
import sk.janobono.wiwa.dal.repository.OrderAttributeRepository;
import sk.janobono.wiwa.model.OrderAttributeKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class OrderCommentUtilServiceTest {

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private OrderCommentUtilService orderCommentUtilService;

    @BeforeEach
    void setUp() {
        final Map<Long, OrderAttributeDo> attributeMap = new HashMap<>();

        orderCommentUtilService = new OrderCommentUtilService(objectMapper, new OrderAttributeRepository() {
            @Override
            public List<OrderAttributeDo> findAllByOrderId(final Long orderId) {
                if (attributeMap.containsKey(orderId)) {
                    return List.of(attributeMap.get(orderId));
                } else {
                    return List.of();
                }
            }

            @Override
            public Optional<OrderAttributeDo> findByOrderIdAndAttributeKey(final Long orderId, final OrderAttributeKey orderAttributeKey) {
                return attributeMap.entrySet().stream().filter(entry -> entry.getKey().equals(orderId)).map(Map.Entry::getValue).findFirst();
            }

            @Override
            public OrderAttributeDo save(final OrderAttributeDo orderAttributeDo) {
                attributeMap.put(orderAttributeDo.getOrderId(), orderAttributeDo);
                return orderAttributeDo;
            }
        });
    }

    @Test
    void fullTest() {
        final Long orderId = 1L;
        final OrderUserData user = OrderUserData.builder().id(1L).build();

        List<OrderCommentData> comments = orderCommentUtilService.addOrderComment(orderId, user, null, "comment01");

        assertThat(comments).isNotNull();
        assertThat(comments.size()).isEqualTo(1);

        comments = orderCommentUtilService.addOrderComment(orderId, user, null, "comment02");

        assertThat(comments).isNotNull();
        assertThat(comments.size()).isEqualTo(2);

        comments = orderCommentUtilService.addOrderComment(orderId, user, 1L, "comment01-1");

        assertThat(comments).isNotNull();
        assertThat(comments.size()).isEqualTo(2);
        assertThat(comments.get(0).subComments()).isNotNull();
        assertThat(comments.get(0).subComments().size()).isEqualTo(1);

        comments = orderCommentUtilService.addOrderComment(orderId, user, 3L, "comment01-1-1");

        assertThat(comments).isNotNull();
        assertThat(comments.size()).isEqualTo(2);
        assertThat(comments.get(0).subComments()).isNotNull();
        assertThat(comments.get(0).subComments().size()).isEqualTo(1);
        assertThat(comments.get(0).subComments().get(0).subComments()).isNotNull();
        assertThat(comments.get(0).subComments().get(0).subComments().size()).isEqualTo(1);
    }
}
