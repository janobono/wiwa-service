package sk.janobono.wiwa.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.api.model.board.BoardChangeWebDto;
import sk.janobono.wiwa.api.model.board.BoardWebDto;
import sk.janobono.wiwa.business.model.board.BoardChangeData;
import sk.janobono.wiwa.business.model.board.BoardData;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {CategoryWebMapper.class})
public interface BoardWebMapper {

    BoardWebDto mapToWebDto(BoardData boardData);

    BoardChangeData mapToData(BoardChangeWebDto boardChange);
}
