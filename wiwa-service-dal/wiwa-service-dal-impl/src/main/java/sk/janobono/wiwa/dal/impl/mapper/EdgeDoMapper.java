package sk.janobono.wiwa.dal.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.EdgeDo;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaEdgeDto;
import sk.janobono.wiwa.model.Unit;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {Unit.class})
public interface EdgeDoMapper {

    @Mapping(target = "saleUnit", expression = "java(Unit.valueOf(wiwaEdgeDto.saleUnit()))")
    @Mapping(target = "weightUnit", expression = "java(Unit.valueOf(wiwaEdgeDto.weightUnit()))")
    @Mapping(target = "netWeightUnit", expression = "java(Unit.valueOf(wiwaEdgeDto.netWeightUnit()))")
    @Mapping(target = "widthUnit", expression = "java(Unit.valueOf(wiwaEdgeDto.widthUnit()))")
    @Mapping(target = "thicknessUnit", expression = "java(Unit.valueOf(wiwaEdgeDto.thicknessUnit()))")
    @Mapping(target = "priceUnit", expression = "java(Unit.valueOf(wiwaEdgeDto.priceUnit()))")
    EdgeDo toEdgeDo(WiwaEdgeDto wiwaEdgeDto);

    @Mapping(target = "saleUnit", expression = "java(edgeDo.getSaleUnit().name())")
    @Mapping(target = "weightUnit", expression = "java(edgeDo.getWeightUnit().name())")
    @Mapping(target = "netWeightUnit", expression = "java(edgeDo.getNetWeightUnit().name())")
    @Mapping(target = "widthUnit", expression = "java(edgeDo.getWidthUnit().name())")
    @Mapping(target = "thicknessUnit", expression = "java(edgeDo.getThicknessUnit().name())")
    @Mapping(target = "priceUnit", expression = "java(edgeDo.getPriceUnit().name())")
    WiwaEdgeDto toWiwaEdgeDto(EdgeDo edgeDo);
}
