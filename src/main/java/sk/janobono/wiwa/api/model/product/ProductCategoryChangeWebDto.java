package sk.janobono.wiwa.api.model.product;

import jakarta.validation.constraints.NotNull;

public record ProductCategoryChangeWebDto(@NotNull Long categoryId) {
}
