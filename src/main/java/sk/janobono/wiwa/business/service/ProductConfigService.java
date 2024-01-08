package sk.janobono.wiwa.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.model.WiwaProperty;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductConfigService {

    private final ApplicationPropertyService applicationPropertyService;

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

    public List<Long> getProductCategories() {
        return applicationPropertyService.getPropertyValue(WiwaProperty.PRODUCT_CATEGORIES)
                .map(value -> Arrays.stream(value.split(",")).map(Long::valueOf).toList())
                .orElse(null);
    }

    public List<Long> setProductCategories(final List<Long> data) {
        applicationPropertyService.setApplicationProperty(WiwaProperty.PRODUCT_CATEGORIES.getGroup(),
                WiwaProperty.PRODUCT_CATEGORIES.getKey(), data.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(","))
        );
        return data;
    }

    public Long getBoardCategoryId() {
        return applicationPropertyService.getPropertyValue(WiwaProperty.PRODUCT_BOARD_CATEGORY_ID)
                .map(Long::valueOf)
                .orElse(null);
    }

    public Long setBoardCategoryId(final Long value) {
        applicationPropertyService.setApplicationProperty(WiwaProperty.PRODUCT_BOARD_CATEGORY_ID.getGroup(),
                WiwaProperty.PRODUCT_BOARD_CATEGORY_ID.getKey(), value.toString());
        return value;
    }

    public Long getEdgeCategoryId() {
        return applicationPropertyService.getPropertyValue(WiwaProperty.PRODUCT_EDGE_CATEGORY_ID)
                .map(Long::valueOf)
                .orElse(null);
    }

    public Long setEdgeCategoryId(final Long value) {
        applicationPropertyService.setApplicationProperty(WiwaProperty.PRODUCT_EDGE_CATEGORY_ID.getGroup(),
                WiwaProperty.PRODUCT_EDGE_CATEGORY_ID.getKey(), value.toString());
        return value;
    }

    public Long getServiceCategoryId() {
        return applicationPropertyService.getPropertyValue(WiwaProperty.PRODUCT_SERVICE_CATEGORY_ID)
                .map(Long::valueOf)
                .orElse(null);
    }

    public Long setServiceCategoryId(final Long value) {
        applicationPropertyService.setApplicationProperty(WiwaProperty.PRODUCT_SERVICE_CATEGORY_ID.getGroup(),
                WiwaProperty.PRODUCT_SERVICE_CATEGORY_ID.getKey(), value.toString());
        return value;
    }

    public List<Long> getSearchCategories() {
        return applicationPropertyService.getPropertyValue(WiwaProperty.PRODUCT_SEARCH_CATEGORIES)
                .map(value -> Arrays.stream(value.split(",")).map(Long::valueOf).toList())
                .orElse(null);
    }

    public List<Long> setSearchCategories(final List<Long> data) {
        applicationPropertyService.setApplicationProperty(WiwaProperty.PRODUCT_SEARCH_CATEGORIES.getGroup(),
                WiwaProperty.PRODUCT_SEARCH_CATEGORIES.getKey(), data.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(","))
        );
        return data;
    }
}
