package sk.janobono.wiwa.business.impl.model.html;

import lombok.Builder;

@Builder
public record HtmlItemBoardData(
        String position,
        String material,
        String name,
        String dimX,
        String dimY,
        String image
) {
}
