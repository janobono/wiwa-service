package sk.janobono.wiwa.api.model.codelist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CodeListItemChangeWebDto(
        @NotNull Long codeListId,
        Long parentId,
        @NotBlank @Size(max = 255) String code,
        @NotBlank @Size(max = 255) String value
) {
}
