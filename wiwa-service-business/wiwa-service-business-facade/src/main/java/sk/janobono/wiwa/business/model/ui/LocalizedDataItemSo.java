package sk.janobono.wiwa.business.model.ui;

import jakarta.validation.constraints.NotBlank;

public record LocalizedDataItemSo<T>(@NotBlank String language, T data) {
}
