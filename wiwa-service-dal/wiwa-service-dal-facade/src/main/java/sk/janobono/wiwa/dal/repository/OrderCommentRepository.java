package sk.janobono.wiwa.dal.repository;

import sk.janobono.wiwa.dal.domain.OrderCommentDo;

import java.util.List;

public interface OrderCommentRepository {

    List<OrderCommentDo> findAllByOrderId(long orderId);

    OrderCommentDo save(OrderCommentDo orderCommentDo);
}
