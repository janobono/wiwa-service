package sk.janobono.wiwa.dal.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.FreeSaleProductViewDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaFreeSaleProductViewDto;
import sk.janobono.wiwa.model.ProductStockStatus;
import sk.janobono.wiwa.model.Unit;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {ProductStockStatus.class, Unit.class})
public interface FreeSaleProductViewDoMapper {

    @Mapping(target = "stockStatus", expression = "java(ProductStockStatus.valueOf(wiwaFreeSaleProductViewDto.stockStatus()))")
    @Mapping(target = "saleUnit", expression = "java(Unit.valueOf(wiwaFreeSaleProductViewDto.saleUnit()))")
    @Mapping(target = "weightUnit", expression = "java(Unit.valueOf(wiwaFreeSaleProductViewDto.weightUnit()))")
    @Mapping(target = "netWeightUnit", expression = "java(Unit.valueOf(wiwaFreeSaleProductViewDto.netWeightUnit()))")
    @Mapping(target = "lengthUnit", expression = "java(Unit.valueOf(wiwaFreeSaleProductViewDto.lengthUnit()))")
    @Mapping(target = "widthUnit", expression = "java(Unit.valueOf(wiwaFreeSaleProductViewDto.widthUnit()))")
    @Mapping(target = "thicknessUnit", expression = "java(Unit.valueOf(wiwaFreeSaleProductViewDto.thicknessUnit()))")
    @Mapping(target = "priceUnit", expression = "java(Unit.valueOf(wiwaFreeSaleProductViewDto.priceUnit()))")
    FreeSaleProductViewDo toFreeSaleProductViewDo(WiwaFreeSaleProductViewDto wiwaFreeSaleProductViewDto);
}
