package sk.janobono.wiwa.business.impl.component.image;

import sk.janobono.wiwa.business.model.application.OrderPropertiesData;
import sk.janobono.wiwa.business.model.order.OrderItemImageData;
import sk.janobono.wiwa.business.model.order.part.PartDuplicatedFrameData;

import java.util.List;

public class PartDuplicatedFrameImageUtil extends BaseImageUtil<PartDuplicatedFrameData> {

    @Override
    public List<OrderItemImageData> generateImages(OrderPropertiesData orderProperties, PartDuplicatedFrameData part) {
        return List.of();
    }
}
