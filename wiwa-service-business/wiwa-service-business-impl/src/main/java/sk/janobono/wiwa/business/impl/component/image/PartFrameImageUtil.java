package sk.janobono.wiwa.business.impl.component.image;

import sk.janobono.wiwa.business.model.application.OrderPropertiesData;
import sk.janobono.wiwa.business.model.order.OrderItemImageData;
import sk.janobono.wiwa.business.model.order.part.PartFrameData;

import java.util.List;

public class PartFrameImageUtil extends BaseImageUtil<PartFrameData> {

    @Override
    public List<OrderItemImageData> generateImages(OrderPropertiesData orderProperties, PartFrameData part) {
        return List.of();
    }
}
