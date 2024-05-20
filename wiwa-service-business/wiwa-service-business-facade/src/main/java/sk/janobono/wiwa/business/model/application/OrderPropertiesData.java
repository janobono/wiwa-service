package sk.janobono.wiwa.business.model.application;

import sk.janobono.wiwa.model.*;

import java.util.Map;

public record OrderPropertiesData(
        Map<BoardDimension, String> dimensions,
        Map<BoardPosition, String> boards,
        Map<EdgePosition, String> edges,
        Map<CornerPosition, String> corners,
        Map<OrderFormat, String> format,
        Map<OrderContent, String> content,
        Map<OrderPackageType, String> packageType,
        String csvSeparator,
        Map<String, String> csvReplacements,
        Map<CSVColumn, String> csvColumns
) {
}
