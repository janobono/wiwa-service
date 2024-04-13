package sk.janobono.wiwa.api.model.edge;

import jakarta.validation.constraints.NotNull;

public record EdgeCategoryItemChangeWebDto(@NotNull Long categoryId, @NotNull Long itemId) {
}
