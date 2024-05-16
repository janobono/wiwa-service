package sk.janobono.wiwa.business.model.order;

import sk.janobono.wiwa.model.BoardPosition;

public record OrderItemPartImageData(
        BoardPosition boardPosition,
        String mimeType,
        byte[] image
) {
}
