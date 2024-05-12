package sk.janobono.wiwa.business.impl.model.pdf;

import lombok.Builder;

@Builder
public record PdfCutSummaryData(
        String thickness,
        String amount,
        String price,
        String vatPrice
) {
}
