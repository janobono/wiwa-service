package sk.janobono.wiwa.api.model.order.summary;

public record OrderItemSummaryWebDto(
        OrderItemPartSummaryWebDto partSummary,
        OrderItemPartSummaryWebDto totalSummary
) {
}
