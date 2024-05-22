package sk.janobono.wiwa.business.impl.model.pdf;

import lombok.Builder;

@Builder
public record PdfItemData(
        String partNum,
        String name,
        String description,
        String quantity,
        String image,
        String dimX,
        String dimY,
        String edgeA1,
        String edgeA2,
        String edgeB1,
        String edgeB2,
        String cornerA1B1,
        String cornerA1B2,
        String cornerA2B1,
        String cornerA2B2



) {
}
