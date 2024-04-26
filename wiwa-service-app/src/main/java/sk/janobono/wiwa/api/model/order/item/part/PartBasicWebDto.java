package sk.janobono.wiwa.api.model.order.item.part;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PartBasicWebDto(
        Long boardId,
        Long edgeIdA1,
        Long edgeIdA2,
        Long edgeIdB1,
        Long edgeIdB2,
        BigDecimal dimensionA,
        BigDecimal dimensionB,
        PartCornerWebDto cornerA1B1,
        PartCornerWebDto cornerA1B2,
        PartCornerWebDto cornerA2B1,
        PartCornerWebDto cornerA2B2,
        PartSummaryWebDto summary
) implements PartWebDto {
}
