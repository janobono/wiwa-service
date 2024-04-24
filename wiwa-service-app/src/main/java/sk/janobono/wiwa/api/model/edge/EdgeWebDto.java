package sk.janobono.wiwa.api.model.edge;

import sk.janobono.wiwa.api.model.application.ApplicationImageInfoWebDto;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;

import java.util.List;

public record EdgeWebDto(
        Long id,
        String code,
        String name,
        String description,
        Quantity sale,
        Quantity weight,
        Quantity width,
        Quantity thickness,
        Money price,
        Money vatPrice,
        List<ApplicationImageInfoWebDto> images,
        List<EdgeCategoryItemWebDto> categoryItems
) {
}
