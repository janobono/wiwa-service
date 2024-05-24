package sk.janobono.wiwa.business.impl.model.pdf;

import lombok.Builder;

import java.util.List;

@Builder
public record PdfItemData(
        String partNum,
        String name,
        String dimX,
        String dimY,
        String quantity,
        String description,
        String image,
        List<PdfItemBoardData> boards,
        List<PdfItemEdgeData> edges,
        List<PdfItemCornerData> corners
) {
}
