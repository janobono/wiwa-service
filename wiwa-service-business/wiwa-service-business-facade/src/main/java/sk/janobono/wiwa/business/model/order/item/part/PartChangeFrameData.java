package sk.janobono.wiwa.business.model.order.item.part;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PartChangeFrameData(
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
        BigDecimal dimensionA,
        BigDecimal dimensionB,
        BigDecimal dimensionA1,
        BigDecimal dimensionA2,
        BigDecimal dimensionB1,
        BigDecimal dimensionB2
) implements PartChangeData {
}
