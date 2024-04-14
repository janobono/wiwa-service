package sk.janobono.wiwa.dal.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.OrderDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaOrderDto;
import sk.janobono.wiwa.model.OrderStatus;
import sk.janobono.wiwa.model.Unit;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {OrderStatus.class, Unit.class})
public interface OrderDoMapper {

    @Mapping(target = "status", expression = "java(OrderStatus.valueOf(wiwaOrderDto.status()))")
    @Mapping(target = "weightUnit", expression = "java(Unit.valueOf(wiwaOrderDto.weightUnit()))")
    @Mapping(target = "netWeightUnit", expression = "java(Unit.valueOf(wiwaOrderDto.netWeightUnit()))")
    @Mapping(target = "totalUnit", expression = "java(Unit.valueOf(wiwaOrderDto.totalUnit()))")
    OrderDo toOrderDo(WiwaOrderDto wiwaOrderDto);

    @Mapping(target = "status", expression = "java(orderDo.getStatus().name())")
    @Mapping(target = "weightUnit", expression = "java(orderDo.getWeightUnit().name())")
    @Mapping(target = "netWeightUnit", expression = "java(orderDo.getNetWeightUnit().name())")
    @Mapping(target = "totalUnit", expression = "java(orderDo.getTotalUnit().name())")
    WiwaOrderDto toWiwaOrderDto(OrderDo orderDo);
}
