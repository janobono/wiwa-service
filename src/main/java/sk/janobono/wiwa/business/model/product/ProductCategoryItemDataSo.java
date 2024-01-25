package sk.janobono.wiwa.business.model.product;

import jakarta.validation.constraints.NotNull;

public record ProductCategoryItemDataSo(@NotNull Long categoryId, @NotNull Long itemId) {
}
