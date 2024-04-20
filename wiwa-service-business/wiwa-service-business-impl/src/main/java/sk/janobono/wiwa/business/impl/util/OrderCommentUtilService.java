package sk.janobono.wiwa.business.impl.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.model.order.OrderCommentData;
import sk.janobono.wiwa.business.model.order.OrderUserData;
import sk.janobono.wiwa.dal.domain.OrderAttributeDo;
import sk.janobono.wiwa.dal.repository.OrderAttributeRepository;
import sk.janobono.wiwa.model.OrderAttributeKey;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OrderCommentUtilService {

    @Data
    public static class OrderComment {
        private Long id;
        private Long parentId;
        private OrderUserData creator;
        private LocalDateTime created;
        private String comment;
        List<OrderComment> subComments;

        public List<OrderComment> getSubComments() {
            if (subComments == null) {
                subComments = new ArrayList<>();
            }
            return subComments;
        }
    }

    private final ObjectMapper objectMapper;

    private final OrderAttributeRepository orderAttributeRepository;

    public List<OrderCommentData> getOrderComments(final Long orderId) {
        return getRootOrderComments(orderId).stream().map(this::toOrderCommentData).toList();
    }

    public List<OrderCommentData> addOrderComment(final Long orderId, final OrderUserData orderUserData, final Long parentId, final String comment) {
        final List<OrderComment> rootOrderComments = getRootOrderComments(orderId);
        final Long id = getMaxId(rootOrderComments) + 1;

        final OrderComment orderComment = new OrderComment();
        orderComment.setId(id);
        orderComment.setParentId(parentId);
        orderComment.setCreator(orderUserData);
        orderComment.setCreated(LocalDateTime.now());
        orderComment.setComment(comment);

        if (parentId == null) {
            rootOrderComments.add(orderComment);
        } else {
            final OrderComment parent = getParent(rootOrderComments, parentId).orElseThrow();
            parent.getSubComments().add(orderComment);
        }

        return setRootOrderComments(orderId, rootOrderComments).stream().map(this::toOrderCommentData).toList();
    }

    private List<OrderComment> getRootOrderComments(final Long orderId) {
        final List<OrderComment> orderComments = new ArrayList<>();

        final String attributeValue = orderAttributeRepository.findByOrderIdAndAttributeKey(orderId, OrderAttributeKey.COMMENTS)
                .map(OrderAttributeDo::getAttributeValue)
                .orElse("");
        if (!attributeValue.isBlank()) {
            try {
                orderComments.addAll(Arrays.asList(objectMapper.readValue(attributeValue, OrderComment[].class)));
            } catch (final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return orderComments;
    }

    private List<OrderComment> setRootOrderComments(final Long orderId, final List<OrderComment> rootOrderComments) {
        final String attributeValue;
        try {
            attributeValue = objectMapper.writeValueAsString(rootOrderComments);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        orderAttributeRepository.save(OrderAttributeDo.builder()
                .orderId(orderId)
                .attributeKey(OrderAttributeKey.COMMENTS)
                .attributeValue(attributeValue)
                .build());

        return rootOrderComments;
    }

    private OrderCommentData toOrderCommentData(final OrderComment orderComment) {
        return OrderCommentData.builder()
                .id(orderComment.getId())
                .parentId(orderComment.getParentId())
                .creator(orderComment.getCreator())
                .created(orderComment.getCreated())
                .comment(orderComment.getComment())
                .subComments(orderComment.getSubComments().stream().map(this::toOrderCommentData).toList())
                .build();
    }

    private Long getMaxId(final List<OrderComment> rootOrderComments) {
        Long result = 0L;
        for (final OrderComment orderComment : rootOrderComments) {
            final Long id = getMaxId(orderComment, result);
            if (id > result) {
                result = id;
            }
        }
        return result;
    }

    private Long getMaxId(final OrderComment orderComment, final Long max) {
        Long id = orderComment.getId();
        if (max > id) {
            id = max;
        }
        for (final OrderComment subComment : orderComment.getSubComments()) {
            final Long subId = getMaxId(subComment, id);
            if (subId > id) {
                id = subId;
            }
        }
        return id;
    }

    private Optional<OrderComment> getParent(final List<OrderComment> orderComments, final Long parentId) {
        for (final OrderComment orderComment : orderComments) {
            if (orderComment.getId().equals(parentId)) {
                return Optional.of(orderComment);
            }
            final Optional<OrderComment> parent = getParent(orderComment.getSubComments(), parentId);
            if (parent.isPresent()) {
                return parent;
            }
        }
        return Optional.empty();
    }
}
