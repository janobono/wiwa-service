package sk.janobono.wiwa.business.model.order.part;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PartChangeBasicData(
        Long boardId,
        Long edgeIdA1,
        Long edgeIdA2,
        Long edgeIdB1,
        Long edgeIdB2,
        BigDecimal dimensionA,
        BigDecimal dimensionB,
        PartCornerChangeData cornerA1B1,
        PartCornerChangeData cornerA1B2,
        PartCornerChangeData cornerA2B1,
        PartCornerChangeData cornerA2B2
) implements PartChangeData {
}
