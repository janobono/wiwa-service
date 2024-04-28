package sk.janobono.wiwa.api.model.order.part;

import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.api.model.DimensionsWebDto;

public record PartDuplicatedBasicWebDto(
        @NotNull Boolean rotate,
        @NotNull Long boardId,
        @NotNull Long boardIdBottom,
        Long edgeIdA1,
        Long edgeIdA2,
        Long edgeIdB1,
        Long edgeIdB2,
        @NotNull DimensionsWebDto dimensionsTOP,
        PartCornerWebDto cornerA1B1,
        PartCornerWebDto cornerA1B2,
        PartCornerWebDto cornerA2B1,
        PartCornerWebDto cornerA2B2
) implements PartWebDto {
}
