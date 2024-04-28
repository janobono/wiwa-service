package sk.janobono.wiwa.api.model.order.part;

import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.api.model.DimensionsWebDto;
import sk.janobono.wiwa.model.FrameType;

public record PartDuplicatedFrameWebDto(
        @NotNull FrameType frameType,
        @NotNull Boolean rotate,
        @NotNull Long boardId,
        Long boardIdA1,
        Long boardIdA2,
        Long boardIdB1,
        Long boardIdB2,
        Long edgeIdA1,
        Long edgeIdA1I,
        Long edgeIdA2,
        Long edgeIdA2I,
        Long edgeIdB1,
        Long edgeIdB1I,
        Long edgeIdB2,
        Long edgeIdB2I,
        @NotNull DimensionsWebDto dimensionsTOP,
        DimensionsWebDto dimensionsA1,
        DimensionsWebDto dimensionsA2,
        DimensionsWebDto dimensionsB1,
        DimensionsWebDto dimensionsB2,
        PartCornerWebDto cornerA1B1,
        PartCornerWebDto cornerA1B2,
        PartCornerWebDto cornerA2B1,
        PartCornerWebDto cornerA2B2
) implements PartWebDto {
}
