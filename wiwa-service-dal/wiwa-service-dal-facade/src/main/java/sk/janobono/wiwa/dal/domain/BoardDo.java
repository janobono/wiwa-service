package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;

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
    private BigDecimal weight;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal thickness;
    private BigDecimal price;
}
