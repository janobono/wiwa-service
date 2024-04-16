package sk.janobono.wiwa.dal.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.BoardDo;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaBoardDto;
import sk.janobono.wiwa.model.Unit;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        imports = {Unit.class})
public interface BoardDoMapper {

    @Mapping(target = "saleUnit", expression = "java(Unit.valueOf(wiwaBoardDto.saleUnit()))")
    @Mapping(target = "weightUnit", expression = "java(Unit.valueOf(wiwaBoardDto.weightUnit()))")
    @Mapping(target = "netWeightUnit", expression = "java(Unit.valueOf(wiwaBoardDto.netWeightUnit()))")
    @Mapping(target = "lengthUnit", expression = "java(Unit.valueOf(wiwaBoardDto.lengthUnit()))")
    @Mapping(target = "widthUnit", expression = "java(Unit.valueOf(wiwaBoardDto.widthUnit()))")
    @Mapping(target = "thicknessUnit", expression = "java(Unit.valueOf(wiwaBoardDto.thicknessUnit()))")
    @Mapping(target = "priceUnit", expression = "java(Unit.valueOf(wiwaBoardDto.priceUnit()))")
    BoardDo toBoardDo(WiwaBoardDto wiwaBoardDto);

    @Mapping(target = "saleUnit", expression = "java(boardDo.getSaleUnit().name())")
    @Mapping(target = "weightUnit", expression = "java(boardDo.getWeightUnit().name())")
    @Mapping(target = "netWeightUnit", expression = "java(boardDo.getNetWeightUnit().name())")
    @Mapping(target = "lengthUnit", expression = "java(boardDo.getLengthUnit().name())")
    @Mapping(target = "widthUnit", expression = "java(boardDo.getWidthUnit().name())")
    @Mapping(target = "thicknessUnit", expression = "java(boardDo.getThicknessUnit().name())")
    @Mapping(target = "priceUnit", expression = "java(boardDo.getPriceUnit().name())")
    WiwaBoardDto toWiwaBoardDto(BoardDo boardDo);
}
