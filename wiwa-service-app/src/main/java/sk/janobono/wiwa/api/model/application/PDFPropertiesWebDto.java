package sk.janobono.wiwa.api.model.application;

import jakarta.validation.constraints.NotEmpty;

public record PDFPropertiesWebDto(
        @NotEmpty String titleFormat
) {
}
