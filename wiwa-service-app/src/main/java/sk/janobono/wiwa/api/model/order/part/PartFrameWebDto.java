package sk.janobono.wiwa.api.model.order.part;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PartFrameWebDto(
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
        @NotNull Boolean horizontal,
        @NotNull @Min(0) BigDecimal dimensionA,
        @NotNull @Min(0) BigDecimal dimensionB,
        @NotNull @Min(0) BigDecimal dimensionA1,
        @NotNull @Min(0) BigDecimal dimensionA2,
        @NotNull @Min(0) BigDecimal dimensionB1,
        @NotNull @Min(0) BigDecimal dimensionB2
) implements PartWebDto {
}
