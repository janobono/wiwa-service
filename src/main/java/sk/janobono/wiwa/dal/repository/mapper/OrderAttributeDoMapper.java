package sk.janobono.wiwa.dal.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.OrderAttributeDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaOrderAttributeDto;
import sk.janobono.wiwa.model.OrderAttributeKey;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {OrderAttributeKey.class})
public interface OrderAttributeDoMapper {

    @Mapping(target = "attributeKey", expression = "java(OrderAttributeKey.valueOf(wiwaOrderAttributeDto.attributeKey()))")
    OrderAttributeDo toOrderAttributeDo(WiwaOrderAttributeDto wiwaOrderAttributeDto);

    @Mapping(target = "attributeKey", expression = "java(orderAttributeDo.getAttributeKey().name())")
    WiwaOrderAttributeDto toWiwaOrderAttributeDto(OrderAttributeDo orderAttributeDo);
}
