package sk.janobono.wiwa.api.model.application;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.model.*;

import java.util.Map;

public record OrderPropertiesWebDto(
        @NotNull Map<BoardDimension, String> dimensions,
        @NotNull Map<BoardPosition, String> boards,
        @NotNull Map<EdgePosition, String> edges,
        @NotNull Map<CornerPosition, String> corners,
        @NotNull Map<OrderFormat, String> format,
        @NotNull Map<OrderContent, String> content,
        @NotNull Map<OrderPackageType, String> packageType,
        @NotEmpty String csvSeparator,
        @NotNull Map<String, String> csvReplacements,
        @NotNull Map<CSVColumn, String> csvColumns
) {
}
