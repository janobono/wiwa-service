package sk.janobono.wiwa.dal.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.OrderItemDo;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderItemDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface OrderItemDoMapper {

    OrderItemDo toOrderItemDo(WiwaOrderItemDto wiwaOrderItemDto);

    WiwaOrderItemDto toWiwaOrderItemDto(OrderItemDo orderItemDo);
}
