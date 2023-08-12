package sk.janobono.wiwa.business.model.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductCategoryDataSo(
        Long parentId,
        @NotBlank @Size(max = 255) String code,
        @NotBlank @Size(max = 255) String name
) {
}
