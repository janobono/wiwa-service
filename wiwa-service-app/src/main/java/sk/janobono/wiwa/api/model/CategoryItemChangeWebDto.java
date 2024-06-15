package sk.janobono.wiwa.api.model;

import jakarta.validation.constraints.NotNull;

public record CategoryItemChangeWebDto(@NotNull Long categoryId, @NotNull Long itemId) {
}
