package sk.janobono.wiwa.business.impl.model.html;

import lombok.Builder;
import sk.janobono.wiwa.business.model.order.OrderContactData;

import java.util.List;

@Builder
public record HtmlContentData(
        String title,
        String creator,
        String created,
        String orderNumber,
        String deliveryDate,
        String packageType,
        OrderContactData orderContact,
        HtmlSummaryData summary,
        List<HtmlItemData> items
) {
}
