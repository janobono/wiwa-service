package sk.janobono.wiwa.dal.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaCodeListItemDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CodeListItemDoMapper {

    CodeListItemDo toCodeListItemDo(WiwaCodeListItemDto wiwaCodeListItemDto);

    WiwaCodeListItemDto toWiwaCodeListItemDto(CodeListItemDo codeListItemDo);
}
