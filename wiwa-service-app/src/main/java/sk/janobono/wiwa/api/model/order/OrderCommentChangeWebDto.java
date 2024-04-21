package sk.janobono.wiwa.api.model.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderCommentChangeWebDto(@NotNull Boolean notifyUser, Long parentId, @NotBlank String comment) {
}
