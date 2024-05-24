package sk.janobono.wiwa.business.impl.model.pdf;

import lombok.Builder;

@Builder
public record PdfItemBoardData(
        String position,
        String material,
        String name,
        String dimX,
        String dimY,
        String image
) {
}
