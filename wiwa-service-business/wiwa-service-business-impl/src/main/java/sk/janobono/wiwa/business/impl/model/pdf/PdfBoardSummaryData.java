package sk.janobono.wiwa.business.impl.model.pdf;

import lombok.Builder;

@Builder
public record PdfBoardSummaryData(
        String material,
        String name,
        String area,
        String boardsCount,
        String weight,
        String price,
        String vatPrice
) {
}
