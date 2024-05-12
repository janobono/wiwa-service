package sk.janobono.wiwa.business.impl.model.pdf;

import lombok.Builder;

@Builder
public record PdfEdgeSummaryData(
        String name,
        String length,
        String glueLength,
        String weight,
        String edgePrice,
        String edgeVatPrice,
        String gluePrice,
        String glueVatPrice
) {
}
