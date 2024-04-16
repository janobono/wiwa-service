package sk.janobono.wiwa.dal.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.OrderItemDo;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderItemDto;
import sk.janobono.wiwa.model.Unit;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {Unit.class})
public interface OrderItemDoMapper {

    @Mapping(target = "weightUnit", expression = "java(Unit.valueOf(wiwaOrderItemDto.weightUnit()))")
    @Mapping(target = "netWeightUnit", expression = "java(Unit.valueOf(wiwaOrderItemDto.netWeightUnit()))")
    @Mapping(target = "priceUnit", expression = "java(Unit.valueOf(wiwaOrderItemDto.priceUnit()))")
    @Mapping(target = "amountUnit", expression = "java(Unit.valueOf(wiwaOrderItemDto.amountUnit()))")
    @Mapping(target = "totalUnit", expression = "java(Unit.valueOf(wiwaOrderItemDto.totalUnit()))")
    OrderItemDo toOrderItemDo(WiwaOrderItemDto wiwaOrderItemDto);

    @Mapping(target = "weightUnit", expression = "java(orderItemDo.getWeightUnit().name())")
    @Mapping(target = "netWeightUnit", expression = "java(orderItemDo.getNetWeightUnit().name())")
    @Mapping(target = "priceUnit", expression = "java(orderItemDo.getPriceUnit().name())")
    @Mapping(target = "amountUnit", expression = "java(orderItemDo.getAmountUnit().name())")
    @Mapping(target = "totalUnit", expression = "java(orderItemDo.getTotalUnit().name())")
    WiwaOrderItemDto toWiwaOrderItemDto(OrderItemDo orderItemDo);
}
