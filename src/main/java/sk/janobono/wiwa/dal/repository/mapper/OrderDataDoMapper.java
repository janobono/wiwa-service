package sk.janobono.wiwa.dal.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.OrderDataDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaOrderDataDto;
import sk.janobono.wiwa.model.OrderDataKey;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {OrderDataKey.class})
public interface OrderDataDoMapper {

    @Mapping(target = "key", expression = "java(OrderDataKey.valueOf(wiwaOrderDataDto.key()))")
    OrderDataDo toOrderDataDo(WiwaOrderDataDto wiwaOrderDataDto);

    @Mapping(target = "key", expression = "java(orderDataDo.getKey().name())")
    WiwaOrderDataDto toWiwaOrderDataDto(OrderDataDo orderDataDo);
}
