package sk.janobono.wiwa.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sk.janobono.wiwa.api.mapper.CategoryWebMapper;
import sk.janobono.wiwa.api.mapper.EdgeWebMapper;
import sk.janobono.wiwa.api.model.CategoryItemChangeWebDto;
import sk.janobono.wiwa.api.model.edge.EdgeChangeWebDto;
import sk.janobono.wiwa.api.model.edge.EdgeWebDto;
import sk.janobono.wiwa.business.model.edge.EdgeSearchCriteriaData;
import sk.janobono.wiwa.business.service.EdgeService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class EdgeApiService {

    private final EdgeService edgeService;
    private final EdgeWebMapper edgeWebMapper;
    private final CategoryWebMapper categoryWebMapper;

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
                .widthFrom(widthFrom)
                .widthTo(widthTo)
                .thicknessFrom(thicknessFrom)
                .thicknessTo(thicknessTo)
                .priceFrom(priceFrom)
                .priceTo(priceTo)
                .codeListItems(codeListItems)
                .build();
        return edgeService.getEdges(criteria, pageable).map(edgeWebMapper::mapToWebDto);
    }

    public EdgeWebDto getEdge(final long id) {
        return edgeWebMapper.mapToWebDto(edgeService.getEdge(id));
    }

    public EdgeWebDto addEdge(final EdgeChangeWebDto edgeChange) {
        return edgeWebMapper.mapToWebDto(edgeService.addEdge(edgeWebMapper.mapToData(edgeChange)));
    }

    public EdgeWebDto setEdge(final long id, final EdgeChangeWebDto edgeChange) {
        return edgeWebMapper.mapToWebDto(edgeService.setEdge(id, edgeWebMapper.mapToData(edgeChange)));
    }

    public void deleteEdge(final long id) {
        edgeService.deleteEdge(id);
    }

    public void setEdgeImage(final long id, final MultipartFile multipartFile) {
        edgeService.setEdgeImage(id, multipartFile);
    }

    public void deleteEdgeImage(final long id) {
        edgeService.deleteEdgeImage(id);
    }

    public EdgeWebDto setEdgeCategoryItems(final long id, final Set<CategoryItemChangeWebDto> categoryItems) {
        return edgeWebMapper.mapToWebDto(
                edgeService.setEdgeCategoryItems(id, categoryItems.stream().map(categoryWebMapper::mapToData).toList())
        );
    }
}
