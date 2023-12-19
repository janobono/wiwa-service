package sk.janobono.wiwa.dal.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.QuantityUnitDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaQuantityUnitDto;
import sk.janobono.wiwa.model.QuantityType;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {QuantityType.class})
public interface QuantityUnitMapper {

    @Mapping(target = "type", expression = "java(QuantityType.valueOf(wiwaQuantityUnitDto.type()))")
    QuantityUnitDo toQuantityUnitDo(WiwaQuantityUnitDto wiwaQuantityUnitDto);

    @Mapping(target = "type", expression = "java(quantityUnitDo.getType().name())")
    WiwaQuantityUnitDto toWiwaQuantityUnitDto(QuantityUnitDo quantityUnitDo);
}
