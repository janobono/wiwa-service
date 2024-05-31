package sk.janobono.wiwa.business.impl.model.html;

import lombok.Builder;

@Builder
public record HtmlCutSummaryData(
        String thickness,
        String amount,
        String price,
        String vatPrice
) {
}
