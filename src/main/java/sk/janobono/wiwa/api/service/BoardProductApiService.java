package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.api.mapper.ProductWebMapper;
import sk.janobono.wiwa.api.model.product.BoardProductWebDto;
import sk.janobono.wiwa.api.model.product.ProductCategoryItemWebDto;
import sk.janobono.wiwa.business.model.product.BoardProductSearchCriteriaData;
import sk.janobono.wiwa.business.service.BoardProductService;
import sk.janobono.wiwa.model.ProductStockStatus;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BoardProductApiService {

    private final BoardProductService boardProductService;
    private final ProductWebMapper productWebMapper;

    public Page<BoardProductWebDto> getBoardProducts(
            final String searchField,
            final String code,
            final String name,
            final ProductStockStatus stockStatus,
            final String boardCode,
            final String structureCode,
            final Boolean orientation,
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
        final BoardProductSearchCriteriaData criteria = BoardProductSearchCriteriaData.builder()
                .searchField(searchField)
                .code(code)
                .name(name)
                .stockStatus(stockStatus)
                .boardCode(boardCode)
                .structureCode(structureCode)
                .orientation(orientation)
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
        return boardProductService.getBoardProducts(criteria, pageable).map(productWebMapper::mapToWebDto);
    }

    public BoardProductWebDto getBoardProduct(final Long id) {
        return productWebMapper.mapToWebDto(boardProductService.getBoardProduct(id));
    }

    public List<ProductCategoryItemWebDto> getSearchItems() {
        return boardProductService.getSearchItems().stream().map(productWebMapper::mapToWebDto).toList();
    }
}
