package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.model.Quantity;

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
    private Quantity sale;
    private Quantity netWeight;
    private Quantity length;
    private Quantity width;
    private Quantity thickness;
    private BigDecimal price;
}
