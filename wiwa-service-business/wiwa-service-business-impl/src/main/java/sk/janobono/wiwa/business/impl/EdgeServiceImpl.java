package sk.janobono.wiwa.business.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.business.impl.component.PriceUtil;
import sk.janobono.wiwa.business.impl.mapper.ApplicationImageDataMapper;
import sk.janobono.wiwa.business.model.application.ApplicationImageInfoData;
import sk.janobono.wiwa.business.model.edge.*;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.business.service.EdgeService;
import sk.janobono.wiwa.component.ImageUtil;
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
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EdgeServiceImpl implements EdgeService {

    private final CommonConfigProperties commonConfigProperties;

    private final ImageUtil imageUtil;
    private final PriceUtil priceUtil;
    private final ScDf scDf;

    private final ApplicationImageDataMapper applicationImageDataMapper;

    private final CodeListRepository codeListRepository;
    private final EdgeRepository edgeRepository;
    private final EdgeImageRepository edgeImageRepository;
    private final EdgeCodeListItemRepository edgeCodeListItemRepository;

    private final ApplicationPropertyService applicationPropertyService;

    @Override
    public Page<EdgeData> getEdges(final EdgeSearchCriteriaData criteria, final Pageable pageable) {
        final BigDecimal vatRate = applicationPropertyService.getVatRate();
        return edgeRepository.findAll(mapToDo(criteria, vatRate), pageable).map(value -> toEdgeData(value, vatRate));
    }

    @Override
    public EdgeData getEdge(final long id) {
        return toEdgeData(getEdgeDo(id), applicationPropertyService.getVatRate());
    }

    @Override
    public EdgeData addEdge(final EdgeChangeData data) {
        if (isCodeUsed(null, data.code())) {
            throw WiwaException.CODE_IS_USED.exception("Code {0} is used", data.code());
        }
        final EdgeDo edgeDo = edgeRepository.save(EdgeDo.builder()
                .code(data.code())
                .name(data.name())
                .description(data.description())
                .weight(data.weight())
                .width(data.width())
                .thickness(data.thickness())
                .price(data.price())
                .build()
        );
        return toEdgeData(edgeDo, applicationPropertyService.getVatRate());
    }

    @Override
    public EdgeData setEdge(final long id, final EdgeChangeData data) {
        final EdgeDo edgeDo = getEdgeDo(id);
        if (isCodeUsed(id, data.code())) {
            throw WiwaException.CODE_IS_USED.exception("Edge code {0} is used", data.code());
        }

        edgeDo.setCode(data.code());
        edgeDo.setName(data.name());
        edgeDo.setDescription(data.description());
        edgeDo.setWeight(data.weight());
        edgeDo.setWidth(data.width());
        edgeDo.setThickness(data.thickness());
        edgeDo.setPrice(data.price());

        return toEdgeData(edgeRepository.save(edgeDo), applicationPropertyService.getVatRate());
    }

    @Override
    public void deleteEdge(final long id) {
        getEdgeDo(id);
        edgeRepository.deleteById(id);
    }

    @Override
    public EdgeData setEdgeImage(final long edgeId, final MultipartFile multipartFile) {
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

        return toEdgeData(getEdgeDo(edgeId), applicationPropertyService.getVatRate());
    }

    @Override
    public EdgeData deleteEdgeImage(final long edgeId, final String fileName) {
        if (!edgeRepository.existsById(edgeId)) {
            throw WiwaException.BOARD_NOT_FOUND.exception("Edge with id {0} not found", edgeId);
        }
        edgeImageRepository.findByEdgeIdAndFileName(edgeId, fileName)
                .ifPresent(edgeImageDo -> edgeImageRepository.deleteById(edgeImageDo.getId()));
        return toEdgeData(getEdgeDo(edgeId), applicationPropertyService.getVatRate());
    }

    @Override
    public EdgeData setEdgeCategoryItems(final long edgeId, final List<EdgeCategoryItemChangeData> categoryItems) {
        final EdgeDo edgeDo = getEdgeDo(edgeId);

        edgeCodeListItemRepository.saveAll(edgeDo.getId(),
                categoryItems.stream()
                        .map(EdgeCategoryItemChangeData::itemId)
                        .toList()
        );

        return toEdgeData(edgeDo, applicationPropertyService.getVatRate());
    }

    private EdgeSearchCriteriaDo mapToDo(final EdgeSearchCriteriaData criteria, final BigDecimal vatRate) {
        return new EdgeSearchCriteriaDo(
                criteria.searchField(),
                criteria.code(),
                criteria.name(),
                criteria.widthFrom(),
                criteria.widthTo(),
                criteria.thicknessFrom(),
                criteria.thicknessTo(),
                priceUtil.countNoVatValue(criteria.priceFrom(), vatRate),
                priceUtil.countNoVatValue(criteria.priceTo(), vatRate),
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
                .sale(new Quantity(BigDecimal.ONE, Unit.METER))
                .weight(new Quantity(edgeDo.getWeight(), Unit.KILOGRAM))
                .width(new Quantity(edgeDo.getWidth(), Unit.MILLIMETER))
                .thickness(new Quantity(edgeDo.getThickness(), Unit.MILLIMETER))
                .price(new Money(edgeDo.getPrice(), commonConfigProperties.currency()))
                .vatPrice(new Money(priceUtil.countVatValue(edgeDo.getPrice(), vatRate), commonConfigProperties.currency()))
                .images(toImages(edgeDo.getId()))
                .categoryItems(toEdgeCategoryItems(edgeDo.getId()))
                .build();
    }

    private List<ApplicationImageInfoData> toImages(final long edgeId) {
        return edgeImageRepository.findAllByEdgeId(edgeId).stream()
                .map(applicationImageDataMapper::mapToData)
                .toList();
    }

    private List<EdgeCategoryItemData> toEdgeCategoryItems(final long edgeId) {
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
}
