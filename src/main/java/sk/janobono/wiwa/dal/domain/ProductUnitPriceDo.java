package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
public class ProductUnitPriceDo {
    private Long id;
    private Long productId;
    private LocalDate validFrom;
    private LocalDate validTo;
    private BigDecimal value;
    private Unit unit;
}
