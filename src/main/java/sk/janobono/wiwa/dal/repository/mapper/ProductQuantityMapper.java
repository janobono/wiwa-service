package sk.janobono.wiwa.dal.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.ProductQuantityDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaProductQuantityDto;
import sk.janobono.wiwa.model.ProductQuantityKey;
import sk.janobono.wiwa.model.Unit;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {ProductQuantityKey.class, Unit.class})
public interface ProductQuantityMapper {

    @Mapping(target = "key", expression = "java(ProductQuantityKey.valueOf(wiwaProductQuantityDto.key()))")
    @Mapping(target = "unit", expression = "java(Unit.valueOf(wiwaProductQuantityDto.unit()))")
    ProductQuantityDo toProductQuantityDo(WiwaProductQuantityDto wiwaProductQuantityDto);

    @Mapping(target = "key", expression = "java(productQuantityDo.getKey().name())")
    @Mapping(target = "unit", expression = "java(productQuantityDo.getUnit().name())")
    WiwaProductQuantityDto toWiwaProductQuantityDto(ProductQuantityDo productQuantityDo);
}
