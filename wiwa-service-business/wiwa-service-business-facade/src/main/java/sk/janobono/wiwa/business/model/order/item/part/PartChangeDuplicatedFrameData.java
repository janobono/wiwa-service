package sk.janobono.wiwa.business.model.order.item.part;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PartChangeDuplicatedFrameData(
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
        BigDecimal dimensionA,
        BigDecimal dimensionB,
        BigDecimal dimensionA1Bottom,
        BigDecimal dimensionA2Bottom,
        BigDecimal dimensionB1Bottom,
        BigDecimal dimensionB2Bottom,
        PartCornerChangeData cornerA1B1,
        PartCornerChangeData cornerA1B2,
        PartCornerChangeData cornerA2B1,
        PartCornerChangeData cornerA2B2
) implements PartChangeData {
}
