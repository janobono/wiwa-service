package sk.janobono.wiwa.api.model.order.item.part;

import sk.janobono.wiwa.model.Quantity;

public record PartDuplicatedBasicWebDto(
        Long boardIdTop,
        Long boardIdBottom,
        Long edgeIdA1,
        Long edgeIdA2,
        Long edgeIdB1,
        Long edgeIdB2,
        Quantity dimensionA,
        Quantity dimensionB,
        PartCornerWebDto cornerA1B1,
        PartCornerWebDto cornerA1B2,
        PartCornerWebDto cornerA2B1,
        PartCornerWebDto cornerA2B2,
        PartSummaryWebDto summary
) implements PartWebDto {
}
