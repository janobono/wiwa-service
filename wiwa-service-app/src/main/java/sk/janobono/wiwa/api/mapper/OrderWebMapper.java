package sk.janobono.wiwa.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.api.model.order.*;
import sk.janobono.wiwa.business.model.order.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {QuantityWebMapper.class})
public interface OrderWebMapper {

    OrderWebDto mapToWebDto(OrderData order);

    OrderCommentWebDto mapToWebDto(OrderCommentData orderComment);

    OrderContactWebDto mapToWebDto(OrderContactData orderContact);

    OrderSummaryWebDto mapToWebDto(OrderSummaryData orderSummary);

    OrderUserWebDto mapToWebDto(OrderUserData orderUser);

    OrderItemWebDto mapToWebDto(OrderItemData orderItem);

    OrderCommentChangeData mapToData(OrderCommentChangeWebDto orderCommentChange);

    OrderStatusChangeData mapToData(OrderStatusChangeWebDto orderStatusChange);

    SendOrderData mapToData(SendOrderWebDto sendOrder);

    OrderItemChangeData mapToData(OrderItemChangeWebDto orderItemChange);

    OrderContactData mapToData(OrderContactWebDto orderContact);
}
