package sk.janobono.wiwa.business.impl.component.image;

import sk.janobono.wiwa.business.model.order.OrderItemPartImageData;
import sk.janobono.wiwa.business.model.order.part.PartFrameData;

import java.util.List;

public class PartFrameImageUtil extends BaseImageUtil<PartFrameData> {

    @Override
    public List<OrderItemPartImageData> generateImages(PartFrameData part) {
        return List.of();
    }
}
