package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.mapper.ApplicationImageDataMapper;
import sk.janobono.wiwa.business.model.ApplicationImageInfoData;
import sk.janobono.wiwa.business.model.product.BoardProductData;
import sk.janobono.wiwa.business.model.product.BoardProductSearchCriteriaData;
import sk.janobono.wiwa.business.model.product.ProductCategoryItemData;
import sk.janobono.wiwa.component.PriceUtil;
import sk.janobono.wiwa.dal.domain.BoardProductViewDo;
import sk.janobono.wiwa.dal.model.BoardProductSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.BoardProductViewRepository;
import sk.janobono.wiwa.dal.repository.ProductImageRepository;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.WiwaProperty;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BoardProductService {

    private final PriceUtil priceUtil;

    private final BoardProductViewRepository boardProductViewRepository;
    private final ProductImageRepository productImageRepository;
    private final ApplicationImageDataMapper applicationImageDataMapper;

    private final ApplicationPropertyService applicationPropertyService;
    private final ProductConfigService productConfigService;

    public Page<BoardProductData> getBoardProducts(final BoardProductSearchCriteriaData criteria, final Pageable pageable) {
        final String categoryItemCode = getCategoryItemCode();
        final BigDecimal vatRate = getVatRate();
        return boardProductViewRepository.findAll(
                categoryItemCode,
                new BoardProductSearchCriteriaDo(
                        criteria.searchField(),
                        criteria.code(),
                        criteria.name(),
                        criteria.stockStatus(),
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
                ),
                pageable
        ).map(boardProductViewDo -> toBoardProductData(boardProductViewDo, vatRate));
    }

    public BoardProductData getBoardProduct(final Long id) {
        final String categoryItemCode = getCategoryItemCode();
        return toBoardProductData(getBoardProductViewDo(id, categoryItemCode), getVatRate());
    }

    public List<ProductCategoryItemData> getSearchItems() {
        return productConfigService.getSearchItems(WiwaProperty.PRODUCT_BOARD_SEARCH_CATEGORIES);
    }

    private String getCategoryItemCode() {
        final ProductCategoryItemData categoryItem = productConfigService.getBoardCategoryItem();
        if (categoryItem == null) {
            throw WiwaException.CODE_LIST_ITEM_NOT_FOUND.exception("Board product category item not found");
        }
        return categoryItem.code();
    }

    private BoardProductViewDo getBoardProductViewDo(final Long id, final String categoryItemCode) {
        return boardProductViewRepository.findByIdAndCategoryItemCode(id, categoryItemCode)
                .orElseThrow(() -> WiwaException.PRODUCT_NOT_FOUND.exception("Product with id {0} not found", id));
    }

    private BigDecimal getVatRate() {
        return new BigDecimal(applicationPropertyService.getProperty(WiwaProperty.PRODUCT_VAT_RATE));
    }

    private BoardProductData toBoardProductData(final BoardProductViewDo boardProductViewDo, final BigDecimal vatRate) {
        return BoardProductData.builder()
                .id(boardProductViewDo.id())
                .code(boardProductViewDo.code())
                .name(boardProductViewDo.name())
                .description(boardProductViewDo.description())
                .stockStatus(boardProductViewDo.stockStatus())
                .boardCode(boardProductViewDo.boardCode())
                .structureCode(boardProductViewDo.structureCode())
                .orientation(boardProductViewDo.orientation())
                .saleValue(boardProductViewDo.saleValue())
                .saleUnit(boardProductViewDo.saleUnit())
                .weightValue(boardProductViewDo.weightValue())
                .weightUnit(boardProductViewDo.weightUnit())
                .netWeightValue(boardProductViewDo.netWeightValue())
                .netWeightUnit(boardProductViewDo.netWeightUnit())
                .lengthValue(boardProductViewDo.lengthValue())
                .lengthUnit(boardProductViewDo.lengthUnit())
                .widthValue(boardProductViewDo.widthValue())
                .widthUnit(boardProductViewDo.widthUnit())
                .thicknessValue(boardProductViewDo.thicknessValue())
                .thicknessUnit(boardProductViewDo.thicknessUnit())
                .priceValue(boardProductViewDo.priceValue())
                .vatPriceValue(priceUtil.countVatValue(boardProductViewDo.priceValue(), vatRate))
                .priceUnit(boardProductViewDo.priceUnit())
                .images(toImages(boardProductViewDo.id()))
                .build();
    }

    private List<ApplicationImageInfoData> toImages(final Long productId) {
        return productImageRepository.findAllByProductId(productId).stream()
                .map(applicationImageDataMapper::mapToData)
                .toList();
    }
}
