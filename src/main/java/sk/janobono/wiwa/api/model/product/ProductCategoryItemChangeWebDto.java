package sk.janobono.wiwa.api.model.product;

import jakarta.validation.constraints.NotNull;

public record ProductCategoryItemChangeWebDto(@NotNull Long categoryId, @NotNull Long itemId) {
}
