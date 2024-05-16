package sk.janobono.wiwa.business.impl.component.image;

import sk.janobono.wiwa.business.model.order.OrderItemPartImageData;
import sk.janobono.wiwa.business.model.order.part.PartBasicData;

import java.util.List;

public class PartBasicImageUtil extends BaseImageUtil<PartBasicData> {

    @Override
    public List<OrderItemPartImageData> generateImages(PartBasicData part) {
        return List.of();
    }
}
