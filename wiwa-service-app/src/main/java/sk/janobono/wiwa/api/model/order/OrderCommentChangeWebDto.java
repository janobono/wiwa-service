package sk.janobono.wiwa.api.model.order;

import jakarta.validation.constraints.NotBlank;

public record OrderCommentChangeWebDto(@NotBlank String comment) {
}
