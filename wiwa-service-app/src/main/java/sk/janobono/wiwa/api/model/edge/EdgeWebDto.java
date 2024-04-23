package sk.janobono.wiwa.api.model.edge;

import sk.janobono.wiwa.api.model.QuantityWebDto;
import sk.janobono.wiwa.api.model.application.ApplicationImageInfoWebDto;
import sk.janobono.wiwa.model.Money;

import java.util.List;

public record EdgeWebDto(
        Long id,
        String code,
        String name,
        String description,
        QuantityWebDto sale,
        QuantityWebDto netWeight,
        QuantityWebDto width,
        QuantityWebDto thickness,
        Money price,
        Money vatPrice,
        List<ApplicationImageInfoWebDto> images,
        List<EdgeCategoryItemWebDto> categoryItems
) {
}
