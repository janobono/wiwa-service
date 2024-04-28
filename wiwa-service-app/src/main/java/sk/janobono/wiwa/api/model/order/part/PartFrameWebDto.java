package sk.janobono.wiwa.api.model.order.part;

import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.api.model.DimensionsWebDto;
import sk.janobono.wiwa.model.FrameType;

public record PartFrameWebDto(
        @NotNull FrameType frameType,
        @NotNull Long boardIdA1,
        @NotNull Long boardIdA2,
        @NotNull Long boardIdB1,
        @NotNull Long boardIdB2,
        Long edgeIdA1,
        Long edgeIdA1I,
        Long edgeIdA2,
        Long edgeIdA2I,
        Long edgeIdB1,
        Long edgeIdB1I,
        Long edgeIdB2,
        Long edgeIdB2I,
        @NotNull DimensionsWebDto dimensionsTOP,
        @NotNull DimensionsWebDto dimensionsA1,
        @NotNull DimensionsWebDto dimensionsA2,
        @NotNull DimensionsWebDto dimensionsB1,
        @NotNull DimensionsWebDto dimensionsB2
) implements PartWebDto {
}
