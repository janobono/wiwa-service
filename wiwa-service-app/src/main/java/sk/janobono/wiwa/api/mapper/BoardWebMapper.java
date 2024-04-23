package sk.janobono.wiwa.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.api.model.board.*;
import sk.janobono.wiwa.business.model.board.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {ApplicationImageWebMapper.class, QuantityWebMapper.class})
public interface BoardWebMapper {

    BoardCategoryWebDto mapToWebDto(BoardCategoryData boardCategory);

    BoardCategoryItemWebDto mapToWebDto(BoardCategoryItemData boardCategoryItem);

    BoardWebDto mapToWebDto(BoardData boardData);

    BoardChangeData mapToData(BoardChangeWebDto boardChange);

    BoardCategoryItemChangeData mapToData(BoardCategoryItemChangeWebDto boardCategoryItemChange);
}
