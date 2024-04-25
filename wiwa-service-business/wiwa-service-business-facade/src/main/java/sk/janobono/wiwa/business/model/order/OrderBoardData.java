package sk.janobono.wiwa.business.model.order;

import lombok.Builder;
import sk.janobono.wiwa.model.Currency;
import sk.janobono.wiwa.model.Quantity;

import java.math.BigDecimal;

@Builder
public record OrderBoardData(
        Long id,
        String code,
        String name,
        String boardCode,
        String structureCode,
        Boolean orientation,
        Quantity weight,
        Quantity length,
        Quantity width,
        Quantity thickness,
        Currency price
) {
}
