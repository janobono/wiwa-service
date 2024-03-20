package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.api.mapper.ProductWebMapper;
import sk.janobono.wiwa.api.model.product.FreeSaleProductWebDto;
import sk.janobono.wiwa.api.model.product.ProductCategoryItemWebDto;
import sk.janobono.wiwa.business.model.product.FreeSaleProductSearchCriteriaData;
import sk.janobono.wiwa.business.service.FreeSaleProductService;
import sk.janobono.wiwa.model.ProductStockStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FreeSaleProductApiService {

    private final FreeSaleProductService freeSaleProductService;
    private final ProductWebMapper productWebMapper;

    public Page<FreeSaleProductWebDto> getFreeSaleProducts(
            final String searchField,
            final String code,
            final String name,
            final ProductStockStatus stockStatus,
            final BigDecimal lengthFrom,
            final BigDecimal lengthTo,
            final Unit lengthUnit,
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
        final FreeSaleProductSearchCriteriaData criteria = FreeSaleProductSearchCriteriaData.builder()
                .searchField(searchField)
                .code(code)
                .name(name)
                .stockStatus(stockStatus)
                .lengthFrom(lengthFrom)
                .lengthTo(lengthTo)
                .lengthUnit(lengthUnit)
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
        return freeSaleProductService.getFreeSaleProducts(criteria, pageable).map(productWebMapper::mapToWebDto);
    }

    public FreeSaleProductWebDto getFreeSaleProduct(final Long id) {
        return productWebMapper.mapToWebDto(freeSaleProductService.getFreeSaleProduct(id));
    }

    public List<ProductCategoryItemWebDto> getSearchItems() {
        return freeSaleProductService.getSearchItems().stream().map(productWebMapper::mapToWebDto).toList();
    }
}
