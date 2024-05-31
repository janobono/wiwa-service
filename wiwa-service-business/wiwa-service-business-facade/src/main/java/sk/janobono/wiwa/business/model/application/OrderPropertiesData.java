package sk.janobono.wiwa.business.model.application;

import lombok.Builder;
import sk.janobono.wiwa.model.*;

import java.util.Map;

@Builder
public record OrderPropertiesData(
        Map<BoardDimension, String> dimensions,
        Map<BoardPosition, String> boards,
        Map<EdgePosition, String> edges,
        Map<CornerPosition, String> corners,
        Map<OrderPattern, String> pattern,
        Map<OrderContent, String> content,
        Map<OrderPackageType, String> packageType,
        String csvSeparator,
        Map<String, String> csvReplacements,
        Map<CSVColumn, String> csvColumns
) {
}
