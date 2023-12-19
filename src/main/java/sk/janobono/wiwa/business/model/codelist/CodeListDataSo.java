package sk.janobono.wiwa.business.model.codelist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CodeListDataSo(
        @NotBlank @Size(max = 255) String code,
        @NotBlank @Size(max = 255) String name
) {
}
