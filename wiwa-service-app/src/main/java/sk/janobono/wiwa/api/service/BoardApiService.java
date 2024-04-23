package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.api.mapper.BoardWebMapper;
import sk.janobono.wiwa.api.model.board.BoardCategoryItemChangeWebDto;
import sk.janobono.wiwa.api.model.board.BoardChangeWebDto;
import sk.janobono.wiwa.api.model.board.BoardWebDto;
import sk.janobono.wiwa.business.model.board.BoardSearchCriteriaData;
import sk.janobono.wiwa.business.service.BoardService;
import sk.janobono.wiwa.model.Quantity;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BoardApiService {

    private final BoardService boardService;
    private final BoardWebMapper boardWebMapper;

    public Page<BoardWebDto> getBoards(
            final String searchField,
            final String code,
            final String name,
            final String boardCode,
            final String structureCode,
            final Boolean orientation,
            final BigDecimal lengthFrom,
            final BigDecimal lengthTo,
            final BigDecimal widthFrom,
            final BigDecimal widthTo,
            final BigDecimal thicknessFrom,
            final BigDecimal thicknessTo,
            final BigDecimal priceFrom,
            final BigDecimal priceTo,
            final List<String> codeListItems,
            final Pageable pageable
    ) {
        final BoardSearchCriteriaData criteria = BoardSearchCriteriaData.builder()
                .searchField(searchField)
                .code(code)
                .name(name)
                .boardCode(boardCode)
                .structureCode(structureCode)
                .orientation(orientation)
                .lengthFrom(Optional.ofNullable(lengthFrom).map(v -> new Quantity(v, Unit.MILLIMETER)).orElse(null))
                .lengthTo(Optional.ofNullable(lengthTo).map(v -> new Quantity(v, Unit.MILLIMETER)).orElse(null))
                .widthFrom(Optional.ofNullable(widthFrom).map(v -> new Quantity(v, Unit.MILLIMETER)).orElse(null))
                .widthTo(Optional.ofNullable(widthTo).map(v -> new Quantity(v, Unit.MILLIMETER)).orElse(null))
                .thicknessFrom(Optional.ofNullable(thicknessFrom).map(v -> new Quantity(v, Unit.MILLIMETER)).orElse(null))
                .thicknessTo(Optional.ofNullable(thicknessTo).map(v -> new Quantity(v, Unit.MILLIMETER)).orElse(null))
                .priceFrom(priceFrom)
                .priceTo(priceTo)
                .codeListItems(codeListItems)
                .build();
        return boardService.getBoards(criteria, pageable).map(boardWebMapper::mapToWebDto);
    }

    public BoardWebDto getBoard(final long id) {
        return boardWebMapper.mapToWebDto(boardService.getBoard(id));
    }

    public BoardWebDto addBoard(final BoardChangeWebDto boardChange) {
        return boardWebMapper.mapToWebDto(boardService.addBoard(boardWebMapper.mapToData(boardChange)));
    }

    public BoardWebDto setBoard(final long id, final BoardChangeWebDto boardChange) {
        return boardWebMapper.mapToWebDto(boardService.setBoard(id, boardWebMapper.mapToData(boardChange)));
    }

    public void deleteBoard(final long id) {
        boardService.deleteBoard(id);
    }

    public BoardWebDto setBoardImage(final long id, final MultipartFile multipartFile) {
        return boardWebMapper.mapToWebDto(boardService.setBoardImage(id, multipartFile));
    }

    public BoardWebDto deleteBoardImage(final long id, final String fileName) {
        return boardWebMapper.mapToWebDto(boardService.deleteBoardImage(id, fileName));
    }

    public BoardWebDto setBoardCategoryItems(final long id, final List<BoardCategoryItemChangeWebDto> categoryItems) {
        return boardWebMapper.mapToWebDto(
                boardService.setBoardCategoryItems(id, categoryItems.stream().map(boardWebMapper::mapToData).toList())
        );
    }
}
