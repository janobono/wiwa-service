package sk.janobono.wiwa.business.impl.model.html;

import lombok.Builder;

@Builder
public record HtmlBoardSummaryData(
        String material,
        String name,
        String area,
        String boardsCount,
        String weight,
        String price,
        String vatPrice
) {
}
