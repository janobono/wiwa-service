package sk.janobono.wiwa.business.impl.model.html;

import lombok.Builder;

import java.util.List;

@Builder
public record HtmlItemData(
        String partNum,
        String name,
        String dimX,
        String dimY,
        String quantity,
        String description,
        String image,
        List<HtmlItemBoardData> boards,
        List<HtmlItemEdgeData> edges,
        List<HtmlItemCornerData> corners
) {
}
