package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;

@Builder
@Data
public class EdgeDo {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Quantity sale;
    private Quantity netWeight;
    private Quantity width;
    private Quantity thickness;
    private Money price;
}
