package sk.janobono.wiwa.business.impl.component;

import org.springframework.stereotype.Component;
import sk.janobono.wiwa.business.model.order.OrderCommentData;
import sk.janobono.wiwa.business.model.order.OrderUserData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class OrderCommentUtil {

    public List<OrderCommentData> addComment(final List<OrderCommentData> comments,
                                             final OrderUserData creator,
                                             final Long parentId,
                                             final String comment) {
        final List<OrderCommentData> result = new ArrayList<>(comments.stream().map(this::unlockSubs).toList());

        final Long id = getMaxId(result) + 1;

        final OrderCommentData orderComment = OrderCommentData.builder()
                .id(id)
                .parentId(parentId)
                .creator(creator)
                .created(LocalDateTime.now())
                .comment(comment)
                .build();

        if (parentId == null) {
            result.add(orderComment);
        } else {
            final OrderCommentData parent = getParent(result, parentId).orElseThrow();
            parent.subComments().add(orderComment);
        }

        return result;
    }

    private OrderCommentData unlockSubs(final OrderCommentData orderCommentData) {
        return OrderCommentData.builder()
                .id(orderCommentData.id())
                .parentId(orderCommentData.parentId())
                .creator(orderCommentData.creator())
                .created(orderCommentData.created())
                .comment(orderCommentData.comment())
                .subComments(Optional.ofNullable(orderCommentData.subComments())
                        .map(subs -> new ArrayList<>(subs.stream().map(this::unlockSubs).toList()))
                        .orElse(new ArrayList<>()))
                .build();
    }

    private Long getMaxId(final List<OrderCommentData> orderComments) {
        Long result = 0L;
        for (final OrderCommentData orderComment : orderComments) {
            final Long id = getMaxId(orderComment, result);
            if (id > result) {
                result = id;
            }
        }
        return result;
    }

    private Long getMaxId(final OrderCommentData orderComment, final Long max) {
        Long id = orderComment.id();
        if (max > id) {
            id = max;
        }

        if (orderComment.subComments() != null) {
            for (final OrderCommentData subComment : orderComment.subComments()) {
                final Long subId = getMaxId(subComment, id);
                if (subId > id) {
                    id = subId;
                }
            }
        }

        return id;
    }

    private Optional<OrderCommentData> getParent(final List<OrderCommentData> orderComments, final Long parentId) {
        for (final OrderCommentData orderComment : orderComments) {

            if (orderComment.id().equals(parentId)) {
                return Optional.of(orderComment);
            }

            if (orderComment.subComments() != null) {
                final Optional<OrderCommentData> parent = getParent(orderComment.subComments(), parentId);
                if (parent.isPresent()) {
                    return parent;
                }
            }
        }
        return Optional.empty();
    }
}
