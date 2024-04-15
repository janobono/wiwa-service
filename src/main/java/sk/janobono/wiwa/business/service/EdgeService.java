package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.business.mapper.ApplicationImageDataMapper;
import sk.janobono.wiwa.business.model.ApplicationImageInfoData;
import sk.janobono.wiwa.business.model.edge.*;
import sk.janobono.wiwa.business.service.util.PropertyUtilService;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.component.PriceUtil;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.domain.EdgeDo;
import sk.janobono.wiwa.dal.domain.EdgeImageDo;
import sk.janobono.wiwa.dal.model.EdgeSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.CodeListRepository;
import sk.janobono.wiwa.dal.repository.EdgeCodeListItemRepository;
import sk.janobono.wiwa.dal.repository.EdgeImageRepository;
import sk.janobono.wiwa.dal.repository.EdgeRepository;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.WiwaProperty;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EdgeService {

    private final CommonConfigProperties commonConfigProperties;

    private final ImageUtil imageUtil;
    private final PriceUtil priceUtil;
    private final ScDf scDf;

    private final ApplicationImageDataMapper applicationImageDataMapper;

    private final CodeListRepository codeListRepository;
    private final EdgeRepository edgeRepository;
    private final EdgeImageRepository edgeImageRepository;
    private final EdgeCodeListItemRepository edgeCodeListItemRepository;

    private final PropertyUtilService propertyUtilService;

    public Page<EdgeData> getEdges(final EdgeSearchCriteriaData criteria, final Pageable pageable) {
        final BigDecimal vatRate = getVatRate();
        return edgeRepository.findAll(mapToDo(criteria, vatRate), pageable).map(value -> toEdgeData(value, vatRate));
    }

    public EdgeData getEdge(final Long id) {
        return toEdgeData(getEdgeDo(id), getVatRate());
    }

    public EdgeData addEdge(final EdgeChangeData data) {
        if (isCodeUsed(null, data.code())) {
            throw WiwaException.CODE_IS_USED.exception("Code {0} is used", data.code());
        }
        final EdgeDo edgeDo = edgeRepository.save(EdgeDo.builder()
                .code(data.code())
                .name(data.name())
                .description(data.description())
                .saleValue(data.saleValue())
                .saleUnit(data.saleUnit())
                .weightValue(data.weightValue())
                .weightUnit(data.weightUnit())
                .netWeightValue(data.netWeightValue())
                .netWeightUnit(data.netWeightUnit())
                .widthValue(data.widthValue())
                .widthUnit(data.widthUnit())
                .thicknessValue(data.thicknessValue())
                .thicknessUnit(data.thicknessUnit())
                .priceValue(data.priceValue())
                .priceUnit(data.priceUnit())
                .build()
        );
        return toEdgeData(edgeDo, getVatRate());
    }

    public EdgeData setEdge(final Long id, final EdgeChangeData data) {
        final EdgeDo edgeDo = getEdgeDo(id);
        if (isCodeUsed(id, data.code())) {
            throw WiwaException.CODE_IS_USED.exception("Edge code {0} is used", data.code());
        }

        edgeDo.setCode(data.code());
        edgeDo.setName(data.name());
        edgeDo.setDescription(data.description());
        edgeDo.setSaleValue(data.saleValue());
        edgeDo.setSaleUnit(data.saleUnit());
        edgeDo.setWeightValue(data.weightValue());
        edgeDo.setWeightUnit(data.weightUnit());
        edgeDo.setNetWeightValue(data.netWeightValue());
        edgeDo.setNetWeightUnit(data.netWeightUnit());
        edgeDo.setWidthValue(data.widthValue());
        edgeDo.setWidthUnit(data.widthUnit());
        edgeDo.setThicknessValue(data.thicknessValue());
        edgeDo.setThicknessUnit(data.thicknessUnit());
        edgeDo.setPriceValue(data.priceValue());
        edgeDo.setPriceUnit(data.priceUnit());

        return toEdgeData(edgeRepository.save(edgeDo), getVatRate());
    }

    public void deleteEdge(final Long id) {
        getEdgeDo(id);
        edgeRepository.deleteById(id);
    }

    public EdgeData setEdgeImage(final Long edgeId, final MultipartFile multipartFile) {
        if (!edgeRepository.existsById(edgeId)) {
            throw WiwaException.BOARD_NOT_FOUND.exception("Edge with id {0} not found", edgeId);
        }

        final String fileName = scDf.toStripAndLowerCase(multipartFile.getOriginalFilename());
        final String fileType = Optional.ofNullable(multipartFile.getContentType()).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        if (!imageUtil.isImageFile(fileType)) {
            throw WiwaException.APPLICATION_IMAGE_NOT_SUPPORTED.exception("Unsupported file type {0}", fileType);
        }

        final EdgeImageDo edgeImageDo = edgeImageRepository.findByEdgeIdAndFileName(edgeId, fileName)
                .orElse(EdgeImageDo.builder().build());
        edgeImageDo.setEdgeId(edgeId);
        edgeImageDo.setFileName(fileName);
        edgeImageDo.setFileType(fileType);
        edgeImageDo.setThumbnail(imageUtil.scaleImage(fileType,
                imageUtil.getFileData(multipartFile),
                commonConfigProperties.maxThumbnailResolution(),
                commonConfigProperties.maxThumbnailResolution()));
        edgeImageDo.setData(imageUtil.scaleImage(fileType,
                imageUtil.getFileData(multipartFile),
                commonConfigProperties.maxImageResolution(),
                commonConfigProperties.maxImageResolution()));
        edgeImageRepository.save(edgeImageDo);

        return toEdgeData(getEdgeDo(edgeId), getVatRate());
    }

    public EdgeData deleteEdgeImage(final Long edgeId, final String fileName) {
        if (!edgeRepository.existsById(edgeId)) {
            throw WiwaException.BOARD_NOT_FOUND.exception("Edge with id {0} not found", edgeId);
        }
        edgeImageRepository.findByEdgeIdAndFileName(edgeId, fileName)
                .ifPresent(edgeImageDo -> edgeImageRepository.deleteById(edgeImageDo.getId()));
        return toEdgeData(getEdgeDo(edgeId), getVatRate());
    }

    public EdgeData setEdgeCategoryItems(final Long edgeId, final List<EdgeCategoryItemChangeData> categoryItems) {
        final EdgeDo edgeDo = getEdgeDo(edgeId);

        edgeCodeListItemRepository.saveAll(edgeDo.getId(),
                categoryItems.stream()
                        .map(EdgeCategoryItemChangeData::itemId)
                        .toList()
        );

        return toEdgeData(edgeDo, getVatRate());
    }

    private EdgeSearchCriteriaDo mapToDo(final EdgeSearchCriteriaData criteria, final BigDecimal vatRate) {
        return new EdgeSearchCriteriaDo(
                criteria.searchField(),
                criteria.code(),
                criteria.name(),
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

    private EdgeDo getEdgeDo(final Long id) {
        return edgeRepository.findById(id)
                .orElseThrow(() -> WiwaException.BOARD_NOT_FOUND.exception("Edge with id {0} not found", id));
    }

    private EdgeData toEdgeData(final EdgeDo edgeDo, final BigDecimal vatRate) {
        return EdgeData.builder()
                .id(edgeDo.getId())
                .code(edgeDo.getCode())
                .name(edgeDo.getName())
                .description(edgeDo.getDescription())
                .saleValue(edgeDo.getSaleValue())
                .saleUnit(edgeDo.getSaleUnit())
                .weightValue(edgeDo.getWeightValue())
                .weightUnit(edgeDo.getWeightUnit())
                .netWeightValue(edgeDo.getNetWeightValue())
                .netWeightUnit(edgeDo.getNetWeightUnit())
                .widthValue(edgeDo.getWidthValue())
                .widthUnit(edgeDo.getWidthUnit())
                .thicknessValue(edgeDo.getThicknessValue())
                .thicknessUnit(edgeDo.getThicknessUnit())
                .priceValue(edgeDo.getPriceValue())
                .vatPriceValue(priceUtil.countVatValue(edgeDo.getPriceValue(), vatRate))
                .priceUnit(edgeDo.getPriceUnit())
                .images(toImages(edgeDo.getId()))
                .categoryItems(toEdgeCategoryItems(edgeDo.getId()))
                .build();
    }

    private List<ApplicationImageInfoData> toImages(final Long edgeId) {
        return edgeImageRepository.findAllByEdgeId(edgeId).stream()
                .map(applicationImageDataMapper::mapToData)
                .toList();
    }

    private List<EdgeCategoryItemData> toEdgeCategoryItems(final Long edgeId) {
        return edgeCodeListItemRepository.findByEdgeId(edgeId).stream()
                .map(this::toEdgeCategoryItem)
                .toList();
    }

    private EdgeCategoryItemData toEdgeCategoryItem(final CodeListItemDo codeListItemDo) {
        final CodeListDo codeList = codeListRepository.findById(codeListItemDo.getCodeListId())
                .orElseThrow(() -> WiwaException.CODE_LIST_NOT_FOUND.exception("Code list with id {0} not found", codeListItemDo.getCodeListId()));
        return new EdgeCategoryItemData(
                codeListItemDo.getId(),
                codeListItemDo.getCode(),
                codeListItemDo.getValue(),
                new EdgeCategoryData(codeList.getId(), codeList.getCode(), codeList.getName())
        );
    }

    private boolean isCodeUsed(final Long id, final String code) {
        return Optional.ofNullable(id).map(edgeId -> edgeRepository.countByIdNotAndCode(edgeId, code) > 0)
                .orElse(edgeRepository.countByCode(code) > 0);
    }

    private BigDecimal getVatRate() {
        return propertyUtilService.getProperty(BigDecimal::new, WiwaProperty.PRODUCT_VAT_RATE);
    }
}
