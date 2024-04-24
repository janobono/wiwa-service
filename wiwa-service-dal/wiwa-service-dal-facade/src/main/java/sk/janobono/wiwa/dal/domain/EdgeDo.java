package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class EdgeDo {
    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal weight;
    private BigDecimal width;
    private BigDecimal thickness;
    private BigDecimal price;
}
