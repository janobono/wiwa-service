package sk.janobono.wiwa.business.impl.model.pdf;

import lombok.Builder;

@Builder
public record PdfGlueSummaryData(String area, String price, String vatPrice) {
}
