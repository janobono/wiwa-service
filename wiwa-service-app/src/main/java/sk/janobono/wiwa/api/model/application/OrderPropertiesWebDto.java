package sk.janobono.wiwa.api.model.application;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import sk.janobono.wiwa.api.model.EntryWebDto;
import sk.janobono.wiwa.model.*;

import java.util.Map;
import java.util.Set;

@Builder
public record OrderPropertiesWebDto(
        @NotNull Set<EntryWebDto<BoardDimension, String>> dimensions,
        @NotNull Set<EntryWebDto<BoardPosition, String>> boards,
        @NotNull Set<EntryWebDto<EdgePosition, String>> edges,
        @NotNull Set<EntryWebDto<CornerPosition, String>> corners,
        @NotNull Set<EntryWebDto<OrderPattern, String>> pattern,
        @NotNull Set<EntryWebDto<OrderContent, String>> content,
        @NotNull Set<EntryWebDto<OrderPackageType, String>> packageType,
        @NotEmpty String csvSeparator,
        @NotNull Set<EntryWebDto<String, String>> csvReplacements,
        @NotNull Set<EntryWebDto<CSVColumn, String>> csvColumns
) {
}
