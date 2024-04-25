package sk.janobono.wiwa.business.model.order.item.part;

import sk.janobono.wiwa.model.Quantity;

public record PartFrameData(
        Long boardIdA1,
        Long boardIdA2,
        Long boardIdB1,
        Long boardIdB2,
        Long edgeIdA1,
        Long edgeIdA1I,
        Long edgeIdA2,
        Long edgeIdA2I,
        Long edgeIdB1,
        Long edgeIdB1I,
        Long edgeIdB2,
        Long edgeIdB2I,
        Boolean horizontal,
        Quantity dimensionA,
        Quantity dimensionB,
        Quantity dimensionA1,
        Quantity dimensionA2,
        Quantity dimensionB1,
        Quantity dimensionB2,
        PartSummaryData summary
) implements PartData {
}
