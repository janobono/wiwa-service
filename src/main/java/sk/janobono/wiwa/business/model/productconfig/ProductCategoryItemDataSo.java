package sk.janobono.wiwa.business.model.productconfig;

import jakarta.validation.constraints.NotNull;

public record ProductCategoryItemDataSo(@NotNull Long categoryId, Long itemId) {
}
