package sk.janobono.wiwa.api.model.board;

import jakarta.validation.constraints.NotNull;

public record BoardCategoryItemChangeWebDto(@NotNull Long categoryId, @NotNull Long itemId) {
}
