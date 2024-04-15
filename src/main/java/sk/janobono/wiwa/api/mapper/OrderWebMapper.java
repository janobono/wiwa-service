package sk.janobono.wiwa.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.api.model.order.OrderWebDto;
import sk.janobono.wiwa.business.model.order.OrderData;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface OrderWebMapper {

    OrderWebDto mapToWebDto(OrderData orderData);
}
