package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.api.mapper.ProductWebMapper;
import sk.janobono.wiwa.api.model.product.EdgeProductWebDto;
import sk.janobono.wiwa.api.model.product.ProductCategoryItemWebDto;
import sk.janobono.wiwa.business.model.product.EdgeProductSearchCriteriaData;
import sk.janobono.wiwa.business.service.EdgeProductService;
import sk.janobono.wiwa.model.ProductStockStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EdgeProductApiService {

    private final EdgeProductService edgeProductService;
    private final ProductWebMapper productWebMapper;

    public Page<EdgeProductWebDto> getEdgeProducts(
            final String searchField,
            final String code,
            final String name,
            final ProductStockStatus stockStatus,
            final BigDecimal widthFrom,
            final BigDecimal widthTo,
            final Unit widthUnit,
            final BigDecimal thicknessFrom,
            final BigDecimal thicknessTo,
            final Unit thicknessUnit,
            final BigDecimal priceFrom,
            final BigDecimal priceTo,
            final Unit priceUnit,
            final List<String> codeListItems,
            final Pageable pageable
    ) {
        final EdgeProductSearchCriteriaData criteria = EdgeProductSearchCriteriaData.builder()
                .searchField(searchField)
                .code(code)
                .name(name)
                .stockStatus(stockStatus)
                .widthFrom(widthFrom)
                .widthTo(widthTo)
                .widthUnit(widthUnit)
                .thicknessFrom(thicknessFrom)
                .thicknessTo(thicknessTo)
                .thicknessUnit(thicknessUnit)
                .priceFrom(priceFrom)
                .priceTo(priceTo)
                .priceUnit(priceUnit)
                .codeListItems(codeListItems)
                .build();
        return edgeProductService.getEdgeProducts(criteria, pageable).map(productWebMapper::mapToWebDto);
    }

    public EdgeProductWebDto getEdgeProduct(final Long id) {
        return productWebMapper.mapToWebDto(edgeProductService.getEdgeProduct(id));
    }

    public List<ProductCategoryItemWebDto> getSearchItems() {
        return edgeProductService.getSearchItems().stream().map(productWebMapper::mapToWebDto).toList();
    }
}
