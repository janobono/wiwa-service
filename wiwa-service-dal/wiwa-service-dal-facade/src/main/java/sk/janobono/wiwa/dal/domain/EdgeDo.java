package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;

@Builder
@Data
public class EdgeDo {
    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal saleValue;
    private Unit saleUnit;
    private BigDecimal weightValue;
    private Unit weightUnit;
    private BigDecimal netWeightValue;
    private Unit netWeightUnit;
    private BigDecimal widthValue;
    private Unit widthUnit;
    private BigDecimal thicknessValue;
    private Unit thicknessUnit;
    private BigDecimal priceValue;
    private Unit priceUnit;
}
