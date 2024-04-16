package sk.janobono.wiwa.dal.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaCodeListDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CodeListDoMapper {

    CodeListDo toCodeListDo(WiwaCodeListDto wiwaCodeListDto);

    WiwaCodeListDto toWiwaCodeListDto(CodeListDo codeListDo);
}
