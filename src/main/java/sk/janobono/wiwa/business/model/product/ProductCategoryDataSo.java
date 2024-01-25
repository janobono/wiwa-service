package sk.janobono.wiwa.business.model.product;

import jakarta.validation.constraints.NotNull;

public record ProductCategoryDataSo(@NotNull Long categoryId) {
}
