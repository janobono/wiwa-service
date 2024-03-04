package sk.janobono.wiwa.api.model.product;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.model.ProductAttributeKey;

public record ProductAttributeWebDto(@NotNull ProductAttributeKey key, @NotEmpty String value) {
}
