package sk.janobono.wiwa.business.model.order.item.part;

import sk.janobono.wiwa.model.Quantity;

public record PartDuplicatedBasicData(
        Long boardIdTop,
        Long boardIdBottom,
        Long edgeIdA1,
        Long edgeIdA2,
        Long edgeIdB1,
        Long edgeIdB2,
        Quantity dimensionA,
        Quantity dimensionB,
        PartCornerData cornerA1B1,
        PartCornerData cornerA1B2,
        PartCornerData cornerA2B1,
        PartCornerData cornerA2B2,
        PartSummaryData summary
) implements PartData {
}
