package sk.janobono.wiwa.dal.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.BoardProductViewDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaBoardProductViewDto;
import sk.janobono.wiwa.model.ProductStockStatus;
import sk.janobono.wiwa.model.Unit;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {ProductStockStatus.class, Unit.class})
public interface BoardProductViewDoMapper {

    @Mapping(target = "orientation", expression = "java(Boolean.valueOf(wiwaBoardProductViewDto.orientation()))")
    @Mapping(target = "stockStatus", expression = "java(ProductStockStatus.valueOf(wiwaBoardProductViewDto.stockStatus()))")
    @Mapping(target = "saleUnit", expression = "java(Unit.valueOf(wiwaBoardProductViewDto.saleUnit()))")
    @Mapping(target = "weightUnit", expression = "java(Unit.valueOf(wiwaBoardProductViewDto.weightUnit()))")
    @Mapping(target = "netWeightUnit", expression = "java(Unit.valueOf(wiwaBoardProductViewDto.netWeightUnit()))")
    @Mapping(target = "lengthUnit", expression = "java(Unit.valueOf(wiwaBoardProductViewDto.lengthUnit()))")
    @Mapping(target = "widthUnit", expression = "java(Unit.valueOf(wiwaBoardProductViewDto.widthUnit()))")
    @Mapping(target = "thicknessUnit", expression = "java(Unit.valueOf(wiwaBoardProductViewDto.thicknessUnit()))")
    @Mapping(target = "priceUnit", expression = "java(Unit.valueOf(wiwaBoardProductViewDto.priceUnit()))")
    BoardProductViewDo toBoardProductViewDo(WiwaBoardProductViewDto wiwaBoardProductViewDto);
}
