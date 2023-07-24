package sk.janobono.wiwa.business.model.ui;

import jakarta.validation.constraints.NotBlank;

public record ApplicationInfoItemSo(@NotBlank String title, @NotBlank String text, @NotBlank String imageFileName) {
}
