package sk.janobono.wiwa.model;

import java.math.BigDecimal;

public record Money(BigDecimal amount, Unit currency) {
    public Money(final BigDecimal amount) {
        this(amount, Unit.EUR);
    }
}
