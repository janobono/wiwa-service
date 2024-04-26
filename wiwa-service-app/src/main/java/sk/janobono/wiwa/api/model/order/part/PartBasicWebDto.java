package sk.janobono.wiwa.api.model.order.part;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PartBasicWebDto(
        @NotNull Long boardId,
        Long edgeIdA1,
        Long edgeIdA2,
        Long edgeIdB1,
        Long edgeIdB2,
        @NotNull @Min(0) BigDecimal dimensionA,
        @NotNull @Min(0) BigDecimal dimensionB,
        PartCornerWebDto cornerA1B1,
        PartCornerWebDto cornerA1B2,
        PartCornerWebDto cornerA2B1,
        PartCornerWebDto cornerA2B2
) implements PartWebDto {
}
