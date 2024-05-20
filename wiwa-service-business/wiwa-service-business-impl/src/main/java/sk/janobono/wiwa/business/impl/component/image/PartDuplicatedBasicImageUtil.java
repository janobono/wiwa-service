package sk.janobono.wiwa.business.impl.component.image;

import sk.janobono.wiwa.business.model.application.OrderPropertiesData;
import sk.janobono.wiwa.business.model.order.OrderItemImageData;
import sk.janobono.wiwa.business.model.order.part.PartDuplicatedBasicData;

import java.util.List;

public class PartDuplicatedBasicImageUtil extends BaseImageUtil<PartDuplicatedBasicData> {

    @Override
    public List<OrderItemImageData> generateImages(OrderPropertiesData orderProperties, PartDuplicatedBasicData part) {
        return List.of();
    }
}
