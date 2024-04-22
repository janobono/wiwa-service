package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.api.mapper.EdgeWebMapper;
import sk.janobono.wiwa.api.model.edge.EdgeCategoryItemChangeWebDto;
import sk.janobono.wiwa.api.model.edge.EdgeChangeWebDto;
import sk.janobono.wiwa.api.model.edge.EdgeWebDto;
import sk.janobono.wiwa.business.model.edge.EdgeSearchCriteriaData;
import sk.janobono.wiwa.business.service.EdgeService;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EdgeApiService {

    private final EdgeService edgeService;
    private final EdgeWebMapper edgeWebMapper;

    public Page<EdgeWebDto> getEdges(
            final String searchField,
            final String code,
            final String name,
            final BigDecimal widthFrom,
            final BigDecimal widthTo,
            final BigDecimal thicknessFrom,
            final BigDecimal thicknessTo,
            final BigDecimal priceFrom,
            final BigDecimal priceTo,
            final List<String> codeListItems,
            final Pageable pageable
    ) {
        final EdgeSearchCriteriaData criteria = EdgeSearchCriteriaData.builder()
                .searchField(searchField)
                .code(code)
                .name(name)
                .widthFrom(Optional.ofNullable(widthFrom).map(v -> new Quantity(v, Unit.MILLIMETER)).orElse(null))
                .widthTo(Optional.ofNullable(widthTo).map(v -> new Quantity(v, Unit.MILLIMETER)).orElse(null))
                .thicknessFrom(Optional.ofNullable(thicknessFrom).map(v -> new Quantity(v, Unit.MILLIMETER)).orElse(null))
                .thicknessTo(Optional.ofNullable(thicknessTo).map(v -> new Quantity(v, Unit.MILLIMETER)).orElse(null))
                .priceFrom(Optional.ofNullable(priceFrom).map(v -> new Money(v, Unit.EUR)).orElse(null))
                .priceTo(Optional.ofNullable(priceTo).map(v -> new Money(v, Unit.EUR)).orElse(null))
                .codeListItems(codeListItems)
                .build();
        return edgeService.getEdges(criteria, pageable).map(edgeWebMapper::mapToWebDto);
    }

    public EdgeWebDto getEdge(final Long id) {
        return edgeWebMapper.mapToWebDto(edgeService.getEdge(id));
    }

    public EdgeWebDto addEdge(final EdgeChangeWebDto edgeChange) {
        return edgeWebMapper.mapToWebDto(edgeService.addEdge(edgeWebMapper.mapToData(edgeChange)));
    }

    public EdgeWebDto setEdge(final Long id, final EdgeChangeWebDto edgeChange) {
        return edgeWebMapper.mapToWebDto(edgeService.setEdge(id, edgeWebMapper.mapToData(edgeChange)));
    }

    public void deleteEdge(final Long id) {
        edgeService.deleteEdge(id);
    }

    public EdgeWebDto setEdgeImage(final Long id, final MultipartFile multipartFile) {
        return edgeWebMapper.mapToWebDto(edgeService.setEdgeImage(id, multipartFile));
    }

    public EdgeWebDto deleteEdgeImage(final Long id, final String fileName) {
        return edgeWebMapper.mapToWebDto(edgeService.deleteEdgeImage(id, fileName));
    }

    public EdgeWebDto setEdgeCategoryItems(final Long id, final List<EdgeCategoryItemChangeWebDto> categoryItems) {
        return edgeWebMapper.mapToWebDto(
                edgeService.setEdgeCategoryItems(id, categoryItems.stream().map(edgeWebMapper::mapToData).toList())
        );
    }
}
