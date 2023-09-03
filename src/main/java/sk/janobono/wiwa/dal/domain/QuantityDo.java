package sk.janobono.wiwa.dal.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Embeddable
public class QuantityDo {
    private BigDecimal value;
    private String unit;
}
