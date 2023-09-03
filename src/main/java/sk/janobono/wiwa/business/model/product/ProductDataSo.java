package sk.janobono.wiwa.business.model.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sk.janobono.wiwa.model.ProductStockStatus;
import sk.janobono.wiwa.model.ProductType;
import sk.janobono.wiwa.model.Quantity;

public record ProductDataSo(
        @NotNull ProductType type,
        @NotBlank @Size(max = 255) String code,
        @Size(max = 255) String boardCode,
        @Size(max = 255) String structureCode,
        @NotBlank @Size(max = 255) String name,
        String note,
        @NotNull Quantity saleUnit,
        Quantity weight,
        Quantity netWeight,
        Quantity length,
        Quantity width,
        Quantity thickness,
        Boolean orientation,
        @NotNull ProductStockStatus stockStatus
) {
}
