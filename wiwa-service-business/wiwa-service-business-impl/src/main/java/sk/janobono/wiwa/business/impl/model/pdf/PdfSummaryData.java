package sk.janobono.wiwa.business.impl.model.pdf;

import lombok.Builder;

import java.util.List;

@Builder
public record PdfSummaryData(
        List<PdfBoardSummaryData> boardSummary,
        List<PdfEdgeSummaryData> edgeSummary,
        PdfGlueSummaryData glueSummary,
        List<PdfCutSummaryData> cutSummary,
        String weight,
        String total,
        String vatTotal
) {
}
