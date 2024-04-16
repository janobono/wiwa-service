package sk.janobono.wiwa.dal.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.dal.domain.BoardImageDo;
import sk.janobono.wiwa.dal.impl.r3n.dto.WiwaBoardImageDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface BoardImageDoMapper {

    BoardImageDo toBoardImageDo(WiwaBoardImageDto wiwaBoardImageDto);

    WiwaBoardImageDto toWiwaBoardImageDto(BoardImageDo boardImageDo);
}
