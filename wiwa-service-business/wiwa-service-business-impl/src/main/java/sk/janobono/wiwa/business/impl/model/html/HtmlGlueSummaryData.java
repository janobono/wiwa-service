package sk.janobono.wiwa.business.impl.model.html;

import lombok.Builder;

@Builder
public record HtmlGlueSummaryData(String area, String price, String vatPrice) {
}
