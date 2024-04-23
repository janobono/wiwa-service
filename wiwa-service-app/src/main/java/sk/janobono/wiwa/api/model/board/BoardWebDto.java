package sk.janobono.wiwa.api.model.board;

import sk.janobono.wiwa.api.model.QuantityWebDto;
import sk.janobono.wiwa.api.model.application.ApplicationImageInfoWebDto;
import sk.janobono.wiwa.model.Money;

import java.util.List;

public record BoardWebDto(
        Long id,
        String code,
        String name,
        String description,
        String boardCode,
        String structureCode,
        Boolean orientation,
        QuantityWebDto sale,
        QuantityWebDto netWeight,
        QuantityWebDto length,
        QuantityWebDto width,
        QuantityWebDto thickness,
        Money price,
        Money vatPrice,
        List<ApplicationImageInfoWebDto> images,
        List<BoardCategoryItemWebDto> categoryItems
) {
}
