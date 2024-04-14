package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;

@Builder
@Data
public class OrderItemDo {
    private Long id;
    private Long orderId;
    private String name;
    private Integer sortNum;
    private String description;
    private BigDecimal weightValue;
    private Unit weightUnit;
    private BigDecimal netWeightValue;
    private Unit netWeightUnit;
    private BigDecimal priceValue;
    private Unit priceUnit;
    private BigDecimal amountValue;
    private Unit amountUnit;
    private BigDecimal totalValue;
    private Unit totalUnit;
}
