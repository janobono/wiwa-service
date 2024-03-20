package sk.janobono.wiwa.dal.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.EdgeProductViewDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaEdgeProductViewDto;
import sk.janobono.wiwa.model.ProductStockStatus;
import sk.janobono.wiwa.model.Unit;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {ProductStockStatus.class, Unit.class})
public interface EdgeProductViewDoMapper {

    @Mapping(target = "stockStatus", expression = "java(ProductStockStatus.valueOf(wiwaEdgeProductViewDto.stockStatus()))")
    @Mapping(target = "saleUnit", expression = "java(Unit.valueOf(wiwaEdgeProductViewDto.saleUnit()))")
    @Mapping(target = "weightUnit", expression = "java(Unit.valueOf(wiwaEdgeProductViewDto.weightUnit()))")
    @Mapping(target = "netWeightUnit", expression = "java(Unit.valueOf(wiwaEdgeProductViewDto.netWeightUnit()))")
    @Mapping(target = "widthUnit", expression = "java(Unit.valueOf(wiwaEdgeProductViewDto.widthUnit()))")
    @Mapping(target = "thicknessUnit", expression = "java(Unit.valueOf(wiwaEdgeProductViewDto.thicknessUnit()))")
    @Mapping(target = "priceUnit", expression = "java(Unit.valueOf(wiwaEdgeProductViewDto.priceUnit()))")
    EdgeProductViewDo toEdgeProductViewDo(WiwaEdgeProductViewDto wiwaEdgeProductViewDto);
}
