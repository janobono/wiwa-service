package sk.janobono.wiwa.dal.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.OrderItemAttributeDo;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaOrderItemAttributeDto;
import sk.janobono.wiwa.model.OrderItemAttributeKey;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {OrderItemAttributeKey.class})
public interface OrderItemAttributeDoMapper {

    @Mapping(target = "attributeKey", expression = "java(OrderItemAttributeKey.valueOf(wiwaOrderItemAttributeDto.attributeKey()))")
    OrderItemAttributeDo toOrderItemAttributeDo(WiwaOrderItemAttributeDto wiwaOrderItemAttributeDto);

    @Mapping(target = "attributeKey", expression = "java(orderItemAttributeDo.getAttributeKey().name())")
    WiwaOrderItemAttributeDto toWiwaOrderItemAttributeDto(OrderItemAttributeDo orderItemAttributeDo);
}
