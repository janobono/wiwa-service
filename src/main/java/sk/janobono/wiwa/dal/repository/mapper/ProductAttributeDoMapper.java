package sk.janobono.wiwa.dal.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.ProductAttributeDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaProductAttributeDto;
import sk.janobono.wiwa.model.ProductAttributeKey;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {ProductAttributeKey.class})
public interface ProductAttributeDoMapper {

    @Mapping(target = "key", expression = "java(ProductAttributeKey.valueOf(wiwaProductAttributeDto.key()))")
    ProductAttributeDo toProductAttributeDo(WiwaProductAttributeDto wiwaProductAttributeDto);

    @Mapping(target = "key", expression = "java(productAttributeDo.getKey().name())")
    WiwaProductAttributeDto toWiwaProductAttributeDto(ProductAttributeDo productAttributeDo);
}
