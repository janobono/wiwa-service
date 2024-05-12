package sk.janobono.wiwa.business.impl.model.pdf;

import lombok.Builder;
import sk.janobono.wiwa.business.model.order.OrderContactData;

@Builder
public record PdfContentData(
        String title,
        String creator,
        String created,
        String orderNumber,
        String deliveryDate,
        String packageType,
        OrderContactData orderContact,
        PdfSummaryData summary
) {
}
