package sk.janobono.wiwa.dal.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.BoardDo;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaBoardDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface BoardDoMapper {

    BoardDo toBoardDo(WiwaBoardDto wiwaBoardDto);

    WiwaBoardDto toWiwaBoardDto(BoardDo boardDo);
}
