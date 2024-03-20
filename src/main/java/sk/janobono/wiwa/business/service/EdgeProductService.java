package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.mapper.ApplicationImageDataMapper;
import sk.janobono.wiwa.business.model.ApplicationImageInfoData;
import sk.janobono.wiwa.business.model.product.EdgeProductData;
import sk.janobono.wiwa.business.model.product.EdgeProductSearchCriteriaData;
import sk.janobono.wiwa.business.model.product.ProductCategoryItemData;
import sk.janobono.wiwa.component.PriceUtil;
import sk.janobono.wiwa.dal.domain.EdgeProductViewDo;
import sk.janobono.wiwa.dal.model.EdgeProductSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.EdgeProductViewRepository;
import sk.janobono.wiwa.dal.repository.ProductImageRepository;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.WiwaProperty;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EdgeProductService {

    private final PriceUtil priceUtil;

    private final EdgeProductViewRepository edgeProductViewRepository;
    private final ProductImageRepository productImageRepository;
    private final ApplicationImageDataMapper applicationImageDataMapper;

    private final ApplicationPropertyService applicationPropertyService;
    private final ProductConfigService productConfigService;

    public Page<EdgeProductData> getEdgeProducts(final EdgeProductSearchCriteriaData criteria, final Pageable pageable) {
        final String categoryItemCode = getCategoryItemCode();
        final BigDecimal vatRate = getVatRate();
        return edgeProductViewRepository.findAll(
                categoryItemCode,
                new EdgeProductSearchCriteriaDo(
                        criteria.searchField(),
                        criteria.code(),
                        criteria.name(),
                        criteria.stockStatus(),
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
        ).map(edgeProductViewDo -> toEdgeProductData(edgeProductViewDo, vatRate));
    }

    public EdgeProductData getEdgeProduct(final Long id) {
        final String categoryItemCode = getCategoryItemCode();
        return toEdgeProductData(getEdgeProductViewDo(id, categoryItemCode), getVatRate());
    }

    public List<ProductCategoryItemData> getSearchItems() {
        return productConfigService.getSearchItems(WiwaProperty.PRODUCT_EDGE_SEARCH_CATEGORIES);
    }

    private String getCategoryItemCode() {
        final ProductCategoryItemData categoryItem = productConfigService.getEdgeCategoryItem();
        if (categoryItem == null) {
            throw WiwaException.CODE_LIST_ITEM_NOT_FOUND.exception("Edge product category item not found");
        }
        return categoryItem.code();
    }

    private EdgeProductViewDo getEdgeProductViewDo(final Long id, final String categoryItemCode) {
        return edgeProductViewRepository.findByIdAndCategoryItemCode(id, categoryItemCode)
                .orElseThrow(() -> WiwaException.PRODUCT_NOT_FOUND.exception("Product with id {0} not found", id));
    }

    private BigDecimal getVatRate() {
        return new BigDecimal(applicationPropertyService.getProperty(WiwaProperty.PRODUCT_VAT_RATE));
    }

    private EdgeProductData toEdgeProductData(final EdgeProductViewDo edgeProductViewDo, final BigDecimal vatRate) {
        return EdgeProductData.builder()
                .id(edgeProductViewDo.id())
                .code(edgeProductViewDo.code())
                .name(edgeProductViewDo.name())
                .description(edgeProductViewDo.description())
                .stockStatus(edgeProductViewDo.stockStatus())
                .saleValue(edgeProductViewDo.saleValue())
                .saleUnit(edgeProductViewDo.saleUnit())
                .weightValue(edgeProductViewDo.weightValue())
                .weightUnit(edgeProductViewDo.weightUnit())
                .netWeightValue(edgeProductViewDo.netWeightValue())
                .netWeightUnit(edgeProductViewDo.netWeightUnit())
                .widthValue(edgeProductViewDo.widthValue())
                .widthUnit(edgeProductViewDo.widthUnit())
                .thicknessValue(edgeProductViewDo.thicknessValue())
                .thicknessUnit(edgeProductViewDo.thicknessUnit())
                .priceValue(edgeProductViewDo.priceValue())
                .vatPriceValue(priceUtil.countVatValue(edgeProductViewDo.priceValue(), vatRate))
                .priceUnit(edgeProductViewDo.priceUnit())
                .images(toImages(edgeProductViewDo.id()))
                .build();
    }

    private List<ApplicationImageInfoData> toImages(final Long productId) {
        return productImageRepository.findAllByProductId(productId).stream()
                .map(applicationImageDataMapper::mapToData)
                .toList();
    }
}
