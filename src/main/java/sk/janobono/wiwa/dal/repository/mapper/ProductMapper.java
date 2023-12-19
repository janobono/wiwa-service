package sk.janobono.wiwa.dal.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.ProductDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaProductDto;
import sk.janobono.wiwa.model.ProductStockStatus;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {ProductStockStatus.class})
public interface ProductMapper {

    @Mapping(target = "stockStatus", expression = "java(ProductStockStatus.valueOf(wiwaProductDto.stockStatus()))")
    ProductDo toProductDo(WiwaProductDto wiwaProductDto);

    @Mapping(target = "stockStatus", expression = "java(productDo.getStockStatus().name())")
    WiwaProductDto toWiwaProductDto(ProductDo productDo);
}
