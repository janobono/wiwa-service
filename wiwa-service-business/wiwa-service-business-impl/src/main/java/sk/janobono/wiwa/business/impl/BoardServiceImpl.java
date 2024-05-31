package sk.janobono.wiwa.business.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.business.impl.component.PriceUtil;
import sk.janobono.wiwa.business.model.board.*;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.business.service.BoardService;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.dal.domain.BoardDo;
import sk.janobono.wiwa.dal.domain.BoardImageDo;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.model.BoardSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.BoardCodeListItemRepository;
import sk.janobono.wiwa.dal.repository.BoardImageRepository;
import sk.janobono.wiwa.dal.repository.BoardRepository;
import sk.janobono.wiwa.dal.repository.CodeListRepository;
import sk.janobono.wiwa.exception.WiwaException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

    private final CommonConfigProperties commonConfigProperties;

    private final ImageUtil imageUtil;
    private final PriceUtil priceUtil;

    private final CodeListRepository codeListRepository;
    private final BoardRepository boardRepository;
    private final BoardImageRepository boardImageRepository;
    private final BoardCodeListItemRepository boardCodeListItemRepository;

    private final ApplicationPropertyService applicationPropertyService;

    @Override
    public Page<BoardData> getBoards(final BoardSearchCriteriaData criteria, final Pageable pageable) {
        final BigDecimal vatRate = applicationPropertyService.getVatRate();
        return boardRepository.findAll(mapToDo(criteria, vatRate), pageable).map(value -> toBoardData(value, vatRate));
    }

    @Override
    public BoardData getBoard(final long id) {
        return toBoardData(getBoardDo(id), applicationPropertyService.getVatRate());
    }

    @Transactional
    @Override
    public BoardData addBoard(final BoardChangeData data) {
        if (isCodeUsed(null, data.code())) {
            throw WiwaException.CODE_IS_USED.exception("Code {0} is used", data.code());
        }
        final BoardDo boardDo = boardRepository.save(BoardDo.builder()
                .code(data.code())
                .name(data.name())
                .description(data.description())
                .boardCode(data.boardCode())
                .structureCode(data.structureCode())
                .orientation(data.orientation())
                .weight(data.weight())
                .length(data.length())
                .width(data.width())
                .thickness(data.thickness())
                .price(data.price())
                .build()
        );
        return toBoardData(boardDo, applicationPropertyService.getVatRate());
    }

    @Transactional
    @Override
    public BoardData setBoard(final long id, final BoardChangeData data) {
        final BoardDo boardDo = getBoardDo(id);
        if (isCodeUsed(id, data.code())) {
            throw WiwaException.CODE_IS_USED.exception("Board code {0} is used", data.code());
        }

        boardDo.setCode(data.code());
        boardDo.setName(data.name());
        boardDo.setDescription(data.description());
        boardDo.setBoardCode(data.boardCode());
        boardDo.setStructureCode(data.structureCode());
        boardDo.setOrientation(data.orientation());
        boardDo.setWeight(data.weight());
        boardDo.setLength(data.length());
        boardDo.setWidth(data.width());
        boardDo.setThickness(data.thickness());
        boardDo.setPrice(data.price());

        return toBoardData(boardRepository.save(boardDo), applicationPropertyService.getVatRate());
    }

    @Transactional
    @Override
    public void deleteBoard(final long id) {
        getBoardDo(id);
        boardRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void setBoardImage(final long boardId, final MultipartFile multipartFile) {
        if (!boardRepository.existsById(boardId)) {
            throw WiwaException.BOARD_NOT_FOUND.exception("Board with id {0} not found", boardId);
        }

        final String fileType = Optional.ofNullable(multipartFile.getContentType()).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        if (!imageUtil.isImageFile(fileType)) {
            throw WiwaException.APPLICATION_IMAGE_NOT_SUPPORTED.exception("Unsupported file type {0}", fileType);
        }

        boardImageRepository.save(BoardImageDo.builder()
                .boardId(boardId)
                .fileType(fileType)
                .data(imageUtil.scaleImage(fileType,
                        imageUtil.getFileData(multipartFile),
                        commonConfigProperties.maxImageResolution(),
                        commonConfigProperties.maxImageResolution())
                )
                .build());
    }

    @Transactional
    @Override
    public void deleteBoardImage(final long boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw WiwaException.BOARD_NOT_FOUND.exception("Board with id {0} not found", boardId);
        }
        boardImageRepository.deleteByBoardId(boardId);
    }

    @Transactional
    @Override
    public BoardData setBoardCategoryItems(final long boardId, final List<BoardCategoryItemChangeData> categoryItems) {
        final BoardDo boardDo = getBoardDo(boardId);

        boardCodeListItemRepository.saveAll(boardDo.getId(),
                categoryItems.stream()
                        .map(BoardCategoryItemChangeData::itemId)
                        .toList()
        );

        return toBoardData(boardDo, applicationPropertyService.getVatRate());
    }

    private BoardSearchCriteriaDo mapToDo(final BoardSearchCriteriaData criteria, final BigDecimal vatRate) {
        return new BoardSearchCriteriaDo(
                criteria.searchField(),
                criteria.code(),
                criteria.name(),
                criteria.boardCode(),
                criteria.structureCode(),
                criteria.orientation(),
                criteria.lengthFrom(),
                criteria.lengthTo(),
                criteria.widthFrom(),
                criteria.widthTo(),
                criteria.thicknessFrom(),
                criteria.thicknessTo(),
                priceUtil.countNoVatValue(criteria.priceFrom(), vatRate),
                priceUtil.countNoVatValue(criteria.priceTo(), vatRate),
                criteria.codeListItems()
        );
    }

    private BoardDo getBoardDo(final Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> WiwaException.BOARD_NOT_FOUND.exception("Board with id {0} not found", id));
    }

    private BoardData toBoardData(final BoardDo boardDo, final BigDecimal vatRate) {
        return BoardData.builder()
                .id(boardDo.getId())
                .code(boardDo.getCode())
                .name(boardDo.getName())
                .description(boardDo.getDescription())
                .boardCode(boardDo.getBoardCode())
                .structureCode(boardDo.getStructureCode())
                .orientation(boardDo.getOrientation())
                .sale(BigDecimal.ONE)
                .weight(boardDo.getWeight())
                .length(boardDo.getLength())
                .width(boardDo.getWidth())
                .thickness(boardDo.getThickness())
                .price(boardDo.getPrice())
                .vatPrice(priceUtil.countVatValue(boardDo.getPrice(), vatRate))
                .categoryItems(toBoardCategoryItems(boardDo.getId()))
                .build();
    }

    private List<BoardCategoryItemData> toBoardCategoryItems(final Long boardId) {
        return boardCodeListItemRepository.findByBoardId(boardId).stream()
                .map(this::toBoardCategoryItem)
                .toList();
    }

    private BoardCategoryItemData toBoardCategoryItem(final CodeListItemDo codeListItemDo) {
        final CodeListDo codeList = codeListRepository.findById(codeListItemDo.getCodeListId())
                .orElseThrow(() -> WiwaException.CODE_LIST_NOT_FOUND.exception("Code list with id {0} not found", codeListItemDo.getCodeListId()));
        return new BoardCategoryItemData(
                codeListItemDo.getId(),
                codeListItemDo.getCode(),
                codeListItemDo.getValue(),
                new BoardCategoryData(codeList.getId(), codeList.getCode(), codeList.getName())
        );
    }

    private boolean isCodeUsed(final Long id, final String code) {
        return Optional.ofNullable(id).map(boardId -> boardRepository.countByIdNotAndCode(boardId, code) > 0)
                .orElse(boardRepository.countByCode(code) > 0);
    }
}
