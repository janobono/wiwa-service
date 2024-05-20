package sk.janobono.wiwa.business.model.order;

import sk.janobono.wiwa.model.ItemImage;

public record OrderItemImageData(
        ItemImage itemImage,
        String mimeType,
        byte[] image
) {
}
