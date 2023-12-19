package sk.janobono.wiwa.dal.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.r3n.dto.WiwaCodeListItemDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CodeListItemMapper {

    CodeListItemDo toCodeListItemDo(WiwaCodeListItemDto wiwaCodeListItemDto);

    WiwaCodeListItemDto toWiwaCodeListItemDto(CodeListItemDo codeListItemDo);
}
