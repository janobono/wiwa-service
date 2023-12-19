package sk.janobono.wiwa.business.model.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sk.janobono.wiwa.model.ProductStockStatus;

import java.util.List;

public record ProductDataSo(
        @NotBlank @Size(max = 255) String code,
        @NotBlank @Size(max = 255) String name,
        String note,
        @NotNull ProductStockStatus stockStatus,
        List<ProductAttributeSo> attributes,
        List<ProductQuantityDataSo> quantities
) {
}
