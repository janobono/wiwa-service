package sk.janobono.wiwa.business.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.model.product.ProductCategoryChangeData;
import sk.janobono.wiwa.business.model.product.ProductCategoryData;
import sk.janobono.wiwa.business.model.product.ProductCategoryItemChangeData;
import sk.janobono.wiwa.business.model.product.ProductCategoryItemData;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.repository.CodeListItemRepository;
import sk.janobono.wiwa.dal.repository.CodeListRepository;
import sk.janobono.wiwa.model.ApplicationPropertyKey;
import sk.janobono.wiwa.model.WiwaProperty;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductConfigService {

    private final ObjectMapper mapper;
    private final ApplicationPropertyService applicationPropertyService;
    private final CodeListRepository codeListRepository;
    private final CodeListItemRepository codeListItemRepository;

    public BigDecimal getVatRate() {
        return applicationPropertyService.getPropertyValue(WiwaProperty.PRODUCT_VAT_RATE)
                .map(BigDecimal::new)
                .orElse(null);
    }

    public BigDecimal setVatRate(final BigDecimal value) {
        applicationPropertyService.setApplicationProperty(WiwaProperty.PRODUCT_VAT_RATE.getGroup(),
                WiwaProperty.PRODUCT_VAT_RATE.getKey(), value.toPlainString());
        return value;
    }

    public List<ProductCategoryData> getProductCategories() {
        return applicationPropertyService.getPropertyValue(WiwaProperty.PRODUCT_CATEGORIES)
                .map(this::toProductCategories)
                .orElse(Collections.emptyList());
    }

    public List<ProductCategoryData> setProductCategories(final List<ProductCategoryChangeData> data) {
        final List<ProductCategoryData> result = data.stream()
                .map(ProductCategoryChangeData::categoryId)
                .map(codeListRepository::findById)
                .flatMap(Optional::stream)
                .map(codeListDo -> new ProductCategoryData(codeListDo.getId(), codeListDo.getCode(), codeListDo.getName()))
                .toList();

        applicationPropertyService.setApplicationProperty(
                WiwaProperty.PRODUCT_CATEGORIES.getGroup(),
                WiwaProperty.PRODUCT_CATEGORIES.getKey(),
                toValue(result)
        );

        return result;
    }

    public ProductCategoryItemData getBoardCategoryItem() {
        return getProductCategoryItem(WiwaProperty.PRODUCT_BOARD_CATEGORY_ITEM);
    }

    public ProductCategoryItemData setBoardCategoryItem(final ProductCategoryItemChangeData data) {
        return setProductCategoryItem(WiwaProperty.PRODUCT_BOARD_CATEGORY_ITEM, data);
    }

    public List<ProductCategoryItemData> setBoardSearchItems(final List<ProductCategoryItemChangeData> data) {
        final List<ProductCategoryItemData> result = data.stream()
                .map(this::toProductCategoryItem)
                .flatMap(Optional::stream)
                .toList();

        applicationPropertyService.setApplicationProperty(
                WiwaProperty.PRODUCT_BOARD_SEARCH_CATEGORIES.getGroup(),
                WiwaProperty.PRODUCT_BOARD_SEARCH_CATEGORIES.getKey(),
                toValue(result)
        );

        return result;
    }

    public ProductCategoryItemData getEdgeCategoryItem() {
        return getProductCategoryItem(WiwaProperty.PRODUCT_EDGE_CATEGORY_ITEM);
    }

    public ProductCategoryItemData setEdgeCategoryItem(final ProductCategoryItemChangeData data) {
        return setProductCategoryItem(WiwaProperty.PRODUCT_EDGE_CATEGORY_ITEM, data);
    }

    public List<ProductCategoryItemData> setEdgeSearchItems(final List<ProductCategoryItemChangeData> data) {
        final List<ProductCategoryItemData> result = data.stream()
                .map(this::toProductCategoryItem)
                .flatMap(Optional::stream)
                .toList();

        applicationPropertyService.setApplicationProperty(
                WiwaProperty.PRODUCT_EDGE_SEARCH_CATEGORIES.getGroup(),
                WiwaProperty.PRODUCT_EDGE_SEARCH_CATEGORIES.getKey(),
                toValue(result)
        );

        return result;
    }

    public ProductCategoryItemData getFreeSaleCategoryItem() {
        return getProductCategoryItem(WiwaProperty.PRODUCT_FREE_SALE_CATEGORY_ITEM);
    }

    public ProductCategoryItemData setFreeSaleCategoryItem(final ProductCategoryItemChangeData data) {
        return setProductCategoryItem(WiwaProperty.PRODUCT_FREE_SALE_CATEGORY_ITEM, data);
    }

    public List<ProductCategoryItemData> setFreeSaleSearchItems(final List<ProductCategoryItemChangeData> data) {
        final List<ProductCategoryItemData> result = data.stream()
                .map(this::toProductCategoryItem)
                .flatMap(Optional::stream)
                .toList();

        applicationPropertyService.setApplicationProperty(
                WiwaProperty.PRODUCT_FREE_SALE_SEARCH_CATEGORIES.getGroup(),
                WiwaProperty.PRODUCT_FREE_SALE_SEARCH_CATEGORIES.getKey(),
                toValue(result)
        );

        return result;
    }

    public List<ProductCategoryItemData> getSearchItems(final ApplicationPropertyKey applicationPropertyKey) {
        return applicationPropertyService.getPropertyValue(applicationPropertyKey)
                .map(this::toProductCategoryItems)
                .orElse(Collections.emptyList());
    }

    private ProductCategoryItemData getProductCategoryItem(final WiwaProperty wiwaProperty) {
        return applicationPropertyService.getPropertyValue(wiwaProperty)
                .map(this::toProductCategoryItem)
                .orElse(null);
    }

    public ProductCategoryItemData setProductCategoryItem(final WiwaProperty wiwaProperty, final ProductCategoryItemChangeData data) {
        final Optional<ProductCategoryItemData> item = toProductCategoryItem(data);

        item.map(pc -> applicationPropertyService.setApplicationProperty(
                wiwaProperty.getGroup(),
                wiwaProperty.getKey(),
                toValue(pc)
        ));

        return item.orElse(null);
    }

    private List<ProductCategoryData> toProductCategories(final String value) {
        try {
            return Arrays.stream(mapper.readValue(value, ProductCategoryData[].class))
                    .filter(productCategory -> codeListRepository.existsById(productCategory.id()))
                    .toList();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<ProductCategoryItemData> toProductCategoryItems(final String value) {
        try {
            return Arrays.stream(mapper.readValue(value, ProductCategoryItemData[].class))
                    .filter(item -> codeListRepository.existsById(item.category().id()))
                    .filter(item -> item.id() == null || codeListItemRepository.existsById(item.id()))
                    .toList();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String toValue(final List<?> list) {
        try {
            return mapper.writeValueAsString(list);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<ProductCategoryItemData> toProductCategoryItem(final ProductCategoryItemChangeData item) {
        final Optional<CodeListDo> codeList = codeListRepository.findById(item.categoryId());
        if (codeList.isEmpty()) {
            return Optional.empty();
        }

        final Optional<CodeListItemDo> codeListItem = codeListItemRepository.findById(item.itemId());
        if (codeListItem.isEmpty()) {
            return Optional.empty();
        }

        return codeListItem.map(ci -> new ProductCategoryItemData(
                ci.getId(),
                ci.getCode(),
                ci.getValue(),
                new ProductCategoryData(codeList.get().getId(), codeList.get().getCode(), codeList.get().getName())
        ));
    }

    private ProductCategoryItemData toProductCategoryItem(final String value) {
        try {
            final ProductCategoryItemData result = mapper.readValue(value, ProductCategoryItemData.class);
            if (codeListRepository.existsById(result.category().id())
                    && codeListItemRepository.existsById(result.id())) {
                return result;
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private String toValue(final ProductCategoryItemData item) {
        try {
            return mapper.writeValueAsString(item);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
