package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.business.mapper.ApplicationImageDataMapper;
import sk.janobono.wiwa.business.model.ApplicationImageInfoData;
import sk.janobono.wiwa.business.model.board.*;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.component.PriceUtil;
import sk.janobono.wiwa.component.ScDf;
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
import sk.janobono.wiwa.model.WiwaProperty;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final CommonConfigProperties commonConfigProperties;

    private final ImageUtil imageUtil;
    private final PriceUtil priceUtil;
    private final ScDf scDf;

    private final ApplicationImageDataMapper applicationImageDataMapper;

    private final CodeListRepository codeListRepository;
    private final BoardRepository boardRepository;
    private final BoardImageRepository boardImageRepository;
    private final BoardCodeListItemRepository boardCodeListItemRepository;

    private final ApplicationPropertyService applicationPropertyService;

    public Page<BoardData> getBoards(final BoardSearchCriteriaData criteria, final Pageable pageable) {
        final BigDecimal vatRate = getVatRate();
        return boardRepository.findAll(mapToDo(criteria, vatRate), pageable).map(value -> toBoardData(value, vatRate));
    }

    public BoardData getBoard(final Long id) {
        return toBoardData(getBoardDo(id), getVatRate());
    }

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
                .saleValue(data.saleValue())
                .saleUnit(data.saleUnit())
                .weightValue(data.weightValue())
                .weightUnit(data.weightUnit())
                .netWeightValue(data.netWeightValue())
                .netWeightUnit(data.netWeightUnit())
                .lengthValue(data.lengthValue())
                .lengthUnit(data.lengthUnit())
                .widthValue(data.widthValue())
                .widthUnit(data.widthUnit())
                .thicknessValue(data.thicknessValue())
                .thicknessUnit(data.thicknessUnit())
                .priceValue(data.priceValue())
                .priceUnit(data.priceUnit())
                .build()
        );
        return toBoardData(boardDo, getVatRate());
    }

    public BoardData setBoard(final Long id, final BoardChangeData data) {
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
        boardDo.setSaleValue(data.saleValue());
        boardDo.setSaleUnit(data.saleUnit());
        boardDo.setWeightValue(data.weightValue());
        boardDo.setWeightUnit(data.weightUnit());
        boardDo.setNetWeightValue(data.netWeightValue());
        boardDo.setNetWeightUnit(data.netWeightUnit());
        boardDo.setLengthValue(data.lengthValue());
        boardDo.setLengthUnit(data.lengthUnit());
        boardDo.setWidthValue(data.widthValue());
        boardDo.setWidthUnit(data.widthUnit());
        boardDo.setThicknessValue(data.thicknessValue());
        boardDo.setThicknessUnit(data.thicknessUnit());
        boardDo.setPriceValue(data.priceValue());
        boardDo.setPriceUnit(data.priceUnit());

        return toBoardData(boardRepository.save(boardDo), getVatRate());
    }

    public void deleteBoard(final Long id) {
        getBoardDo(id);
        boardRepository.deleteById(id);
    }

    public BoardData setBoardImage(final Long boardId, final MultipartFile multipartFile) {
        if (!boardRepository.existsById(boardId)) {
            throw WiwaException.BOARD_NOT_FOUND.exception("Board with id {0} not found", boardId);
        }

        final String fileName = scDf.toStripAndLowerCase(multipartFile.getOriginalFilename());
        final String fileType = Optional.ofNullable(multipartFile.getContentType()).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        if (!imageUtil.isImageFile(fileType)) {
            throw WiwaException.APPLICATION_IMAGE_NOT_SUPPORTED.exception("Unsupported file type {0}", fileType);
        }

        final BoardImageDo boardImageDo = boardImageRepository.findByBoardIdAndFileName(boardId, fileName)
                .orElse(BoardImageDo.builder().build());
        boardImageDo.setBoardId(boardId);
        boardImageDo.setFileName(fileName);
        boardImageDo.setFileType(fileType);
        boardImageDo.setThumbnail(imageUtil.scaleImage(fileType,
                imageUtil.getFileData(multipartFile),
                commonConfigProperties.maxThumbnailResolution(),
                commonConfigProperties.maxThumbnailResolution()));
        boardImageDo.setData(imageUtil.scaleImage(fileType,
                imageUtil.getFileData(multipartFile),
                commonConfigProperties.maxImageResolution(),
                commonConfigProperties.maxImageResolution()));
        boardImageRepository.save(boardImageDo);

        return toBoardData(getBoardDo(boardId), getVatRate());
    }

    public BoardData deleteBoardImage(final Long boardId, final String fileName) {
        if (!boardRepository.existsById(boardId)) {
            throw WiwaException.BOARD_NOT_FOUND.exception("Board with id {0} not found", boardId);
        }
        boardImageRepository.findByBoardIdAndFileName(boardId, fileName)
                .ifPresent(boardImageDo -> boardImageRepository.deleteById(boardImageDo.getId()));
        return toBoardData(getBoardDo(boardId), getVatRate());
    }

    public BoardData setBoardCategoryItems(final Long boardId, final List<BoardCategoryItemChangeData> categoryItems) {
        final BoardDo boardDo = getBoardDo(boardId);

        boardCodeListItemRepository.saveAll(boardDo.getId(),
                categoryItems.stream()
                        .map(BoardCategoryItemChangeData::itemId)
                        .toList()
        );

        return toBoardData(boardDo, getVatRate());
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
                criteria.lengthUnit(),
                criteria.widthFrom(),
                criteria.widthTo(),
                criteria.widthUnit(),
                criteria.thicknessFrom(),
                criteria.thicknessTo(),
                criteria.thicknessUnit(),
                priceUtil.countNoVatValue(criteria.priceFrom(), vatRate),
                priceUtil.countNoVatValue(criteria.priceTo(), vatRate),
                criteria.priceUnit(),
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
                .saleValue(boardDo.getSaleValue())
                .saleUnit(boardDo.getSaleUnit())
                .weightValue(boardDo.getWeightValue())
                .weightUnit(boardDo.getWeightUnit())
                .netWeightValue(boardDo.getNetWeightValue())
                .netWeightUnit(boardDo.getNetWeightUnit())
                .lengthValue(boardDo.getLengthValue())
                .lengthUnit(boardDo.getLengthUnit())
                .widthValue(boardDo.getWidthValue())
                .widthUnit(boardDo.getWidthUnit())
                .thicknessValue(boardDo.getThicknessValue())
                .thicknessUnit(boardDo.getThicknessUnit())
                .priceValue(boardDo.getPriceValue())
                .vatPriceValue(priceUtil.countVatValue(boardDo.getPriceValue(), vatRate))
                .priceUnit(boardDo.getPriceUnit())
                .images(toImages(boardDo.getId()))
                .categoryItems(toBoardCategoryItems(boardDo.getId()))
                .build();
    }

    private List<ApplicationImageInfoData> toImages(final Long boardId) {
        return boardImageRepository.findAllByBoardId(boardId).stream()
                .map(applicationImageDataMapper::mapToData)
                .toList();
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

    private BigDecimal getVatRate() {
        return new BigDecimal(applicationPropertyService.getProperty(WiwaProperty.PRODUCT_VAT_RATE));
    }
}
