package sk.janobono.wiwa.business.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.model.product.ProductCategoryDataSo;
import sk.janobono.wiwa.business.model.product.ProductCategoryItemDataSo;
import sk.janobono.wiwa.business.model.product.ProductCategoryItemSo;
import sk.janobono.wiwa.business.model.product.ProductCategorySo;
import sk.janobono.wiwa.dal.domain.CodeListDo;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.repository.CodeListItemRepository;
import sk.janobono.wiwa.dal.repository.CodeListRepository;
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

    public List<ProductCategorySo> getProductCategories() {
        return applicationPropertyService.getPropertyValue(WiwaProperty.PRODUCT_CATEGORIES)
                .map(this::toProductCategories)
                .orElse(Collections.emptyList());
    }

    public List<ProductCategorySo> setProductCategories(final List<ProductCategoryDataSo> data) {
        final List<ProductCategorySo> result = data.stream()
                .map(ProductCategoryDataSo::categoryId)
                .map(codeListRepository::findById)
                .flatMap(Optional::stream)
                .map(codeListDo -> new ProductCategorySo(codeListDo.getId(), codeListDo.getCode() + ":" + codeListDo.getName()))
                .toList();

        applicationPropertyService.setApplicationProperty(
                WiwaProperty.PRODUCT_CATEGORIES.getGroup(),
                WiwaProperty.PRODUCT_CATEGORIES.getKey(),
                toValue(result)
        );

        return result;
    }

    public ProductCategoryItemSo getBoardCategoryItem() {
        return getProductCategoryItem(WiwaProperty.PRODUCT_BOARD_CATEGORY_ITEM);
    }

    public ProductCategoryItemSo setBoardCategoryItem(final ProductCategoryItemDataSo data) {
        return setProductCategoryItem(WiwaProperty.PRODUCT_BOARD_CATEGORY_ITEM, data);
    }

    public ProductCategoryItemSo getEdgeCategoryItem() {
        return getProductCategoryItem(WiwaProperty.PRODUCT_EDGE_CATEGORY_ITEM);
    }

    public ProductCategoryItemSo setEdgeCategoryItem(final ProductCategoryItemDataSo data) {
        return setProductCategoryItem(WiwaProperty.PRODUCT_EDGE_CATEGORY_ITEM, data);
    }

    public ProductCategoryItemSo getServiceCategoryItem() {
        return getProductCategoryItem(WiwaProperty.PRODUCT_SERVICE_CATEGORY_ITEM);
    }

    public ProductCategoryItemSo setServiceCategoryItem(final ProductCategoryItemDataSo data) {
        return setProductCategoryItem(WiwaProperty.PRODUCT_SERVICE_CATEGORY_ITEM, data);
    }

    public List<ProductCategoryItemSo> getSearchItems() {
        return applicationPropertyService.getPropertyValue(WiwaProperty.PRODUCT_SEARCH_CATEGORIES)
                .map(this::toProductCategoryItems)
                .orElse(Collections.emptyList());
    }

    public List<ProductCategoryItemSo> setSearchItems(final List<ProductCategoryItemDataSo> data) {
        final List<ProductCategoryItemSo> result = data.stream()
                .map(this::toProductCategoryItem)
                .flatMap(Optional::stream)
                .toList();

        applicationPropertyService.setApplicationProperty(
                WiwaProperty.PRODUCT_SEARCH_CATEGORIES.getGroup(),
                WiwaProperty.PRODUCT_SEARCH_CATEGORIES.getKey(),
                toValue(result)
        );

        return result;
    }

    private ProductCategoryItemSo getProductCategoryItem(final WiwaProperty wiwaProperty) {
        return applicationPropertyService.getPropertyValue(wiwaProperty)
                .map(this::toProductCategoryItem)
                .orElse(null);
    }

    public ProductCategoryItemSo setProductCategoryItem(final WiwaProperty wiwaProperty, final ProductCategoryItemDataSo data) {
        final Optional<ProductCategoryItemSo> item = toProductCategoryItem(data);

        item.map(pc -> applicationPropertyService.setApplicationProperty(
                wiwaProperty.getGroup(),
                wiwaProperty.getKey(),
                toValue(pc)
        ));

        return item.orElse(null);
    }

    private List<ProductCategorySo> toProductCategories(final String value) {
        try {
            return Arrays.stream(mapper.readValue(value, ProductCategorySo[].class))
                    .filter(productCategory -> codeListRepository.existsById(productCategory.id()))
                    .toList();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<ProductCategoryItemSo> toProductCategoryItems(final String value) {
        try {
            return Arrays.stream(mapper.readValue(value, ProductCategoryItemSo[].class))
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

    private Optional<ProductCategoryItemSo> toProductCategoryItem(final ProductCategoryItemDataSo item) {
        final Optional<CodeListDo> codeList = codeListRepository.findById(item.itemId());
        final Optional<CodeListItemDo> codeListItem = Optional.ofNullable(item.itemId()).stream()
                .map(codeListItemRepository::findById)
                .flatMap(Optional::stream)
                .findFirst();

        return codeList.map(cl -> new ProductCategoryItemSo(
                codeListItem.map(CodeListItemDo::getId).orElse(null),
                codeListItem.map(cli -> cli.getCode() + ":" + cli.getValue()).orElse(null),
                new ProductCategorySo(cl.getId(), cl.getCode() + ":" + cl.getName())
        ));
    }

    private ProductCategoryItemSo toProductCategoryItem(final String value) {
        try {
            final ProductCategoryItemSo result = mapper.readValue(value, ProductCategoryItemSo.class);
            if (codeListRepository.existsById(result.category().id())) {
                if (result.id() == null) {
                    return result;
                }
                if (codeListItemRepository.existsById(result.id())) {
                    return result;
                }
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private String toValue(final ProductCategoryItemSo item) {
        try {
            return mapper.writeValueAsString(item);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
