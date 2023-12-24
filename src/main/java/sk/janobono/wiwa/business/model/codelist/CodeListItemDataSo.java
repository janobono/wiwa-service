package sk.janobono.wiwa.business.model.codelist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CodeListItemDataSo(
        @NotNull Long codeListId,
        Long parentId,
        @NotBlank @Size(max = 255) String code,
        @NotBlank @Size(max = 255) String value
) {
}
