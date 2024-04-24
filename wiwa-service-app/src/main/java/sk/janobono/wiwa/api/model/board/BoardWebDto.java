package sk.janobono.wiwa.api.model.board;

import sk.janobono.wiwa.api.model.application.ApplicationImageInfoWebDto;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;

import java.util.List;

public record BoardWebDto(
        Long id,
        String code,
        String name,
        String description,
        String boardCode,
        String structureCode,
        Boolean orientation,
        Quantity sale,
        Quantity weight,
        Quantity length,
        Quantity width,
        Quantity thickness,
        Money price,
        Money vatPrice,
        List<ApplicationImageInfoWebDto> images,
        List<BoardCategoryItemWebDto> categoryItems
) {
}
