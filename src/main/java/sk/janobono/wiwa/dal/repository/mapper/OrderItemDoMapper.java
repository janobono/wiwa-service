package sk.janobono.wiwa.dal.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.OrderItemDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaOrderItemDto;
import sk.janobono.wiwa.model.OrderItemType;
import sk.janobono.wiwa.model.Unit;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {OrderItemType.class, Unit.class})
public interface OrderItemDoMapper {

    @Mapping(target = "type", expression = "java(OrderItemType.valueOf(wiwaOrderItemDto.type()))")
    @Mapping(target = "priceUnit", expression = "java(Unit.valueOf(wiwaOrderItemDto.priceUnit()))")
    @Mapping(target = "amountUnit", expression = "java(Unit.valueOf(wiwaOrderItemDto.amountUnit()))")
    @Mapping(target = "totalUnit", expression = "java(Unit.valueOf(wiwaOrderItemDto.totalUnit()))")
    OrderItemDo toOrderItemDo(WiwaOrderItemDto wiwaOrderItemDto);

    @Mapping(target = "type", expression = "java(orderItemDo.getType().name())")
    @Mapping(target = "priceUnit", expression = "java(orderItemDo.getPriceUnit().name())")
    @Mapping(target = "amountUnit", expression = "java(orderItemDo.getAmountUnit().name())")
    @Mapping(target = "totalUnit", expression = "java(orderItemDo.getTotalUnit().name())")
    WiwaOrderItemDto toWiwaOrderItemDto(OrderItemDo orderItemDo);
}
