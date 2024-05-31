package sk.janobono.wiwa.business.impl.model.html;

import lombok.Builder;

import java.util.List;

@Builder
public record HtmlSummaryData(
        List<HtmlBoardSummaryData> boardSummary,
        List<HtmlEdgeSummaryData> edgeSummary,
        HtmlGlueSummaryData glueSummary,
        List<HtmlCutSummaryData> cutSummary,
        String weight,
        String total,
        String vatTotal
) {
}
