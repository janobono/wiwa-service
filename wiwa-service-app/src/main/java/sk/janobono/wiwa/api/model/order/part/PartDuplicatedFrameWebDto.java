package sk.janobono.wiwa.api.model.order.part;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PartDuplicatedFrameWebDto(
        @NotNull Long boardIdTop,
        Long boardIdA1Bottom,
        Long boardIdA2Bottom,
        Long boardIdB1Bottom,
        Long boardIdB2Bottom,
        Long edgeIdA1,
        Long edgeIdA1IBottom,
        Long edgeIdA2,
        Long edgeIdA2IBottom,
        Long edgeIdB1,
        Long edgeIdB1IBottom,
        Long edgeIdB2,
        Long edgeIdB2IBottom,
        @NotNull @Min(0) BigDecimal dimensionA,
        @NotNull @Min(0) BigDecimal dimensionB,
        BigDecimal dimensionA1Bottom,
        BigDecimal dimensionA2Bottom,
        BigDecimal dimensionB1Bottom,
        BigDecimal dimensionB2Bottom,
        PartCornerWebDto cornerA1B1,
        PartCornerWebDto cornerA1B2,
        PartCornerWebDto cornerA2B1,
        PartCornerWebDto cornerA2B2
) implements PartWebDto {
}
