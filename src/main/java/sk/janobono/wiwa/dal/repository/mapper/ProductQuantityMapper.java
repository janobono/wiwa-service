package sk.janobono.wiwa.dal.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.ProductQuantityDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaProductQuantityDto;
import sk.janobono.wiwa.model.ProductQuantityKey;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {ProductQuantityKey.class})
public interface ProductQuantityMapper {

    @Mapping(target = "key", expression = "java(ProductQuantityKey.valueOf(wiwaProductQuantityDto.key()))")
    ProductQuantityDo toProductQuantityDo(WiwaProductQuantityDto wiwaProductQuantityDto);

    @Mapping(target = "key", expression = "java(productQuantityDo.getKey().name())")
    WiwaProductQuantityDto toWiwaProductQuantityDto(ProductQuantityDo productQuantityDo);
}
