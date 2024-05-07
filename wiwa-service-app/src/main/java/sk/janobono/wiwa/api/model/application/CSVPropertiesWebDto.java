package sk.janobono.wiwa.api.model.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.model.BoardPosition;
import sk.janobono.wiwa.model.CSVColumn;
import sk.janobono.wiwa.model.CornerPosition;
import sk.janobono.wiwa.model.EdgePosition;

import java.util.Map;

public record CSVPropertiesWebDto(
        @NotBlank String separator,
        @NotNull Map<String, String> replacements,
        @NotNull Map<CSVColumn, String> columns,
        @NotNull Map<BoardPosition, String> boards,
        @NotNull Map<EdgePosition, String> edges,
        @NotNull Map<CornerPosition, String> corners,
        @NotBlank String numberFormat,
        @NotBlank String nameBasicFormat,
        @NotBlank String nameFrameFormat,
        @NotBlank String nameDuplicatedBasicFormat,
        @NotBlank String nameDuplicatedFrameFormat,
        @NotBlank String edgeFormat,
        @NotBlank String cornerStraightFormat,
        @NotBlank String cornerRoundedFormat
) {
}
