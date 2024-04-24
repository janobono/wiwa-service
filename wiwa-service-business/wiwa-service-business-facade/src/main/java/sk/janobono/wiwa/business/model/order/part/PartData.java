package sk.janobono.wiwa.business.model.order.part;

import lombok.Builder;
import sk.janobono.wiwa.model.Quantity;

@Builder
public record PartData(
        Quantity width,
        Quantity length
) {
}
