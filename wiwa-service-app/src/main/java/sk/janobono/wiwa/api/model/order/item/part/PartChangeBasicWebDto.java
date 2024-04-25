package sk.janobono.wiwa.api.model.order.item.part;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PartChangeBasicWebDto(
        @NotNull Long boardId,
        Long edgeIdA1,
        Long edgeIdA2,
        Long edgeIdB1,
        Long edgeIdB2,
        @NotNull @Min(0) BigDecimal dimensionA,
        @NotNull @Min(0) BigDecimal dimensionB,
        PartCornerChangeWebDto cornerA1B1,
        PartCornerChangeWebDto cornerA1B2,
        PartCornerChangeWebDto cornerA2B1,
        PartCornerChangeWebDto cornerA2B2
) implements PartChangeWebDto {
}
