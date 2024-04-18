package sk.janobono.wiwa.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.api.model.order.OrderCommentChangeWebDto;
import sk.janobono.wiwa.api.model.order.OrderContactWebDto;
import sk.janobono.wiwa.api.model.order.OrderUserWebDto;
import sk.janobono.wiwa.api.model.order.OrderWebDto;
import sk.janobono.wiwa.business.model.order.OrderCommentChangeData;
import sk.janobono.wiwa.business.model.order.OrderContactData;
import sk.janobono.wiwa.business.model.order.OrderData;
import sk.janobono.wiwa.business.model.order.OrderUserData;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface OrderWebMapper {

    OrderWebDto mapToWebDto(OrderData orderData);

    OrderUserWebDto mapToWebDto(OrderUserData orderUserData);

    OrderContactWebDto mapToWebDto(OrderContactData orderContactData);

    OrderCommentChangeData mapToData(OrderCommentChangeWebDto orderCommentChange);
}
