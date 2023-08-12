package sk.janobono.wiwa.business.model.product;

import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.ProductStockStatus;
import sk.janobono.wiwa.model.ProductType;
import sk.janobono.wiwa.model.Quantity;

public record ProductSo(
        Long id,
        ProductType type,
        String code,
        String boardCode,
        String structureCode,
        String name,
        String note,
        Quantity saleUnit,
        Money unitPrice,
        Quantity weight,
        Quantity netWeight,
        Quantity length,
        Quantity width,
        Quantity thickness,
        boolean orientation,
        ProductStockStatus stockStatus
) {
}
