package sk.janobono.wiwa.dal.domain;

import lombok.Builder;
import lombok.Data;
import sk.janobono.wiwa.model.QuantityType;

@Builder
@Data
public class QuantityUnitDo {
    private Long id;
    private QuantityType type;
    private String name;
    private String unit;
}
