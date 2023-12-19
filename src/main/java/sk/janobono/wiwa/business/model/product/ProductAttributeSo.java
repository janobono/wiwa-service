package sk.janobono.wiwa.business.model.product;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import sk.janobono.wiwa.model.ProductAttributeKey;

public record ProductAttributeSo(@NotNull ProductAttributeKey key, @NotEmpty String value) {
}
