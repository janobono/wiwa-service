package sk.janobono.wiwa.api.model.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sk.janobono.wiwa.model.ProductStockStatus;

import java.util.List;

public record ProductChangeWebDto(
        @NotBlank @Size(max = 255) String code,
        @NotBlank @Size(max = 255) String name,
        String description,
        @NotNull ProductStockStatus stockStatus,
        List<ProductAttributeWebDto> attributes,
        List<ProductQuantityWebDto> quantities
) {
}
