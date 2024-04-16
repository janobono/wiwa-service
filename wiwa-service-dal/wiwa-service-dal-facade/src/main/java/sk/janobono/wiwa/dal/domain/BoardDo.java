package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;

@Builder
@Data
public class BoardDo {
    private Long id;
    private String code;
    private String boardCode;
    private String structureCode;
    private String name;
    private String description;
    private Boolean orientation;
    private BigDecimal saleValue;
    private Unit saleUnit;
    private BigDecimal weightValue;
    private Unit weightUnit;
    private BigDecimal netWeightValue;
    private Unit netWeightUnit;
    private BigDecimal lengthValue;
    private Unit lengthUnit;
    private BigDecimal widthValue;
    private Unit widthUnit;
    private BigDecimal thicknessValue;
    private Unit thicknessUnit;
    private BigDecimal priceValue;
    private Unit priceUnit;
}
