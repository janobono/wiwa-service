package sk.janobono.wiwa.api.model.codelist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CodeListChangeWebDto(
        @NotBlank @Size(max = 255) String code,
        @NotBlank @Size(max = 255) String name
) {
}
