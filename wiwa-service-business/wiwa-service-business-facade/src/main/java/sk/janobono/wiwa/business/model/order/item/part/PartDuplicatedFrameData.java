package sk.janobono.wiwa.business.model.order.item.part;

import sk.janobono.wiwa.model.Quantity;

public record PartDuplicatedFrameData(
        Long boardIdTop,
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
        Quantity dimensionA,
        Quantity dimensionB,
        Quantity dimensionA1Bottom,
        Quantity dimensionA2Bottom,
        Quantity dimensionB1Bottom,
        Quantity dimensionB2Bottom,
        PartCornerData cornerA1B1,
        PartCornerData cornerA1B2,
        PartCornerData cornerA2B1,
        PartCornerData cornerA2B2,
        PartSummaryData summary
) implements PartData {
}
