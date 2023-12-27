package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
public class ProductUnitPriceDo {
    private Long id;
    private Long productId;
    private Long unitId;
    private LocalDate validFrom;
    private LocalDate validTo;
    private BigDecimal value;
}
