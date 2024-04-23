package sk.janobono.wiwa.dal.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.OrderStatusDo;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderStatusDto;
import sk.janobono.wiwa.model.OrderStatus;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {OrderStatus.class})
public interface OrderStatusDoMapper {

    @Mapping(target = "status", expression = "java(OrderStatus.valueOf(wiwaOrderStatusDto.status()))")
    OrderStatusDo toOrderStatusDo(WiwaOrderStatusDto wiwaOrderStatusDto);

    @Mapping(target = "status", expression = "java(orderStatusDo.getStatus().name())")
    WiwaOrderStatusDto toWiwaOrderStatusDto(OrderStatusDo orderStatusDo);
}
