package sk.janobono.wiwa.model;

import java.math.BigDecimal;

public record Money(BigDecimal value, String unit) {
}
