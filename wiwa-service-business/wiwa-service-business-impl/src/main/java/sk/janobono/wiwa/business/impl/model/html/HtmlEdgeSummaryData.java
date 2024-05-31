package sk.janobono.wiwa.business.impl.model.html;

import lombok.Builder;

@Builder
public record HtmlEdgeSummaryData(
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
