package sk.janobono.wiwa.dal.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.OrderItemDataDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaOrderItemDataDto;
import sk.janobono.wiwa.model.OrderItemDataKey;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {OrderItemDataKey.class})
public interface OrderItemDataDoMapper {

    @Mapping(target = "key", expression = "java(OrderItemDataKey.valueOf(wiwaOrderItemDataDto.key()))")
    OrderItemDataDo toOrderItemDataDo(WiwaOrderItemDataDto wiwaOrderItemDataDto);

    @Mapping(target = "key", expression = "java(orderItemDataDo.getKey().name())")
    WiwaOrderItemDataDto toWiwaOrderItemDataDto(OrderItemDataDo orderItemDataDo);
}
