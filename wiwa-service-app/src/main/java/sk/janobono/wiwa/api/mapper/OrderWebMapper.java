package sk.janobono.wiwa.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.api.model.order.*;
import sk.janobono.wiwa.business.model.order.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface OrderWebMapper {

    OrderWebDto mapToWebDto(OrderData order);

    OrderCommentWebDto mapToWebDto(OrderCommentData orderComment);

    OrderContactWebDto mapToWebDto(OrderContactData orderContact);

    OrderUserWebDto mapToWebDto(OrderUserData orderUser);

    OrderItemDetailWebDto mapToWebDto(OrderItemDetailData orderItemDetail);

    OrderCommentChangeData mapToData(OrderCommentChangeWebDto orderCommentChange);

    OrderStatusChangeData mapToData(OrderStatusChangeWebDto orderStatusChange);

    SendOrderData mapToData(SendOrderWebDto sendOrder);

    OrderItemData mapToData(OrderItemWebDto orderItem);
}
