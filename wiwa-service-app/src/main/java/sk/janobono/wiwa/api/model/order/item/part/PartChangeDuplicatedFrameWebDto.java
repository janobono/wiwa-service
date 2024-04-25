package sk.janobono.wiwa.api.model.order.item.part;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PartChangeDuplicatedFrameWebDto(
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
        PartCornerChangeWebDto cornerA1B1,
        PartCornerChangeWebDto cornerA1B2,
        PartCornerChangeWebDto cornerA2B1,
        PartCornerChangeWebDto cornerA2B2
) implements PartChangeWebDto {
}
