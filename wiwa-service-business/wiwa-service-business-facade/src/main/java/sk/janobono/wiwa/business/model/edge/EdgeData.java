package sk.janobono.wiwa.business.model.edge;

import lombok.Builder;
import sk.janobono.wiwa.business.model.application.ApplicationImageInfoData;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;

import java.util.List;

@Builder
public record EdgeData(
        Long id,
        String code,
        String name,
        String description,
        Quantity sale,
        Quantity weight,
        Quantity netWeight,
        Quantity width,
        Quantity thickness,
        Money price,
        Money vatPrice,
        List<ApplicationImageInfoData> images,
        List<EdgeCategoryItemData> categoryItems
) {
}
