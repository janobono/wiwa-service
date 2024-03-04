package sk.janobono.wiwa.business.model.product;

import sk.janobono.wiwa.model.ProductQuantityKey;
import sk.janobono.wiwa.model.Unit;

import java.math.BigDecimal;

public record ProductQuantityData(ProductQuantityKey key, BigDecimal value, Unit unit) {
}
