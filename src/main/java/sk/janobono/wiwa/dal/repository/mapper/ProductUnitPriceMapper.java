package sk.janobono.wiwa.dal.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.ProductUnitPriceDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaProductUnitPriceDto;
import sk.janobono.wiwa.model.Unit;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {Unit.class})
public interface ProductUnitPriceMapper {

    @Mapping(target = "unit", expression = "java(Unit.valueOf(wiwaProductUnitPriceDto.unit()))")
    ProductUnitPriceDo toProductUnitPriceDo(WiwaProductUnitPriceDto wiwaProductUnitPriceDto);

    @Mapping(target = "unit", expression = "java(productUnitPriceDo.getUnit().name())")
    WiwaProductUnitPriceDto toWiwaProductUnitPriceDto(ProductUnitPriceDo productUnitPriceDo);
}
