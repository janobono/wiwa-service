package sk.janobono.wiwa.business.model.board;

import lombok.Builder;
import sk.janobono.wiwa.model.Money;
import sk.janobono.wiwa.model.Quantity;

import java.util.List;

@Builder
public record BoardData(
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
        List<BoardCategoryItemData> categoryItems
) {
}
