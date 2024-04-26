package sk.janobono.wiwa.business.model.order.part;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PartBasicData(
        Long boardId,
        Long edgeIdA1,
        Long edgeIdA2,
        Long edgeIdB1,
        Long edgeIdB2,
        BigDecimal dimensionA,
        BigDecimal dimensionB,
        PartCornerData cornerA1B1,
        PartCornerData cornerA1B2,
        PartCornerData cornerA2B1,
        PartCornerData cornerA2B2
) implements PartData {
}
