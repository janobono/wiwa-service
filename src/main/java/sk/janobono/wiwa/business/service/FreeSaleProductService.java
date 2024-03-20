package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.mapper.ApplicationImageDataMapper;
import sk.janobono.wiwa.business.model.ApplicationImageInfoData;
import sk.janobono.wiwa.business.model.product.FreeSaleProductData;
import sk.janobono.wiwa.business.model.product.FreeSaleProductSearchCriteriaData;
import sk.janobono.wiwa.business.model.product.ProductCategoryItemData;
import sk.janobono.wiwa.component.PriceUtil;
import sk.janobono.wiwa.dal.domain.FreeSaleProductViewDo;
import sk.janobono.wiwa.dal.model.FreeSaleProductSearchCriteriaDo;
import sk.janobono.wiwa.dal.repository.FreeSaleProductViewRepository;
import sk.janobono.wiwa.dal.repository.ProductImageRepository;
import sk.janobono.wiwa.exception.WiwaException;
import sk.janobono.wiwa.model.WiwaProperty;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FreeSaleProductService {

    private final PriceUtil priceUtil;

    private final FreeSaleProductViewRepository freeSaleProductViewRepository;
    private final ProductImageRepository productImageRepository;
    private final ApplicationImageDataMapper applicationImageDataMapper;

    private final ApplicationPropertyService applicationPropertyService;
    private final ProductConfigService productConfigService;

    public Page<FreeSaleProductData> getFreeSaleProducts(final FreeSaleProductSearchCriteriaData criteria, final Pageable pageable) {
        final String categoryItemCode = getCategoryItemCode();
        final BigDecimal vatRate = getVatRate();
        return freeSaleProductViewRepository.findAll(
                categoryItemCode,
                new FreeSaleProductSearchCriteriaDo(
                        criteria.searchField(),
                        criteria.code(),
                        criteria.name(),
                        criteria.stockStatus(),
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
        ).map(freeSaleProductViewDo -> toFreeSaleProductData(freeSaleProductViewDo, vatRate));
    }

    public FreeSaleProductData getFreeSaleProduct(final Long id) {
        final String categoryItemCode = getCategoryItemCode();
        return toFreeSaleProductData(getFreeSaleProductViewDo(id, categoryItemCode), getVatRate());
    }

    public List<ProductCategoryItemData> getSearchItems() {
        return productConfigService.getSearchItems(WiwaProperty.PRODUCT_FREE_SALE_SEARCH_CATEGORIES);
    }

    private String getCategoryItemCode() {
        final ProductCategoryItemData categoryItem = productConfigService.getFreeSaleCategoryItem();
        if (categoryItem == null) {
            throw WiwaException.CODE_LIST_ITEM_NOT_FOUND.exception("Free sale product category item not found");
        }
        return categoryItem.code();
    }

    private FreeSaleProductViewDo getFreeSaleProductViewDo(final Long id, final String categoryItemCode) {
        return freeSaleProductViewRepository.findByIdAndCategoryItemCode(id, categoryItemCode)
                .orElseThrow(() -> WiwaException.PRODUCT_NOT_FOUND.exception("Product with id {0} not found", id));
    }

    private BigDecimal getVatRate() {
        return new BigDecimal(applicationPropertyService.getProperty(WiwaProperty.PRODUCT_VAT_RATE));
    }

    private FreeSaleProductData toFreeSaleProductData(final FreeSaleProductViewDo freeSaleProductViewDo, final BigDecimal vatRate) {
        return FreeSaleProductData.builder()
                .id(freeSaleProductViewDo.id())
                .code(freeSaleProductViewDo.code())
                .name(freeSaleProductViewDo.name())
                .description(freeSaleProductViewDo.description())
                .stockStatus(freeSaleProductViewDo.stockStatus())
                .saleValue(freeSaleProductViewDo.saleValue())
                .saleUnit(freeSaleProductViewDo.saleUnit())
                .weightValue(freeSaleProductViewDo.weightValue())
                .weightUnit(freeSaleProductViewDo.weightUnit())
                .netWeightValue(freeSaleProductViewDo.netWeightValue())
                .netWeightUnit(freeSaleProductViewDo.netWeightUnit())
                .lengthValue(freeSaleProductViewDo.lengthValue())
                .lengthUnit(freeSaleProductViewDo.lengthUnit())
                .widthValue(freeSaleProductViewDo.widthValue())
                .widthUnit(freeSaleProductViewDo.widthUnit())
                .thicknessValue(freeSaleProductViewDo.thicknessValue())
                .thicknessUnit(freeSaleProductViewDo.thicknessUnit())
                .priceValue(freeSaleProductViewDo.priceValue())
                .vatPriceValue(priceUtil.countVatValue(freeSaleProductViewDo.priceValue(), vatRate))
                .priceUnit(freeSaleProductViewDo.priceUnit())
                .images(toImages(freeSaleProductViewDo.id()))
                .build();
    }

    private List<ApplicationImageInfoData> toImages(final Long productId) {
        return productImageRepository.findAllByProductId(productId).stream()
                .map(applicationImageDataMapper::mapToData)
                .toList();
    }
}
