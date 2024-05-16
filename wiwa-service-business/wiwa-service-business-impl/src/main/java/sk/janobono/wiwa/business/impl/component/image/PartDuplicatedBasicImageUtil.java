package sk.janobono.wiwa.business.impl.component.image;

import sk.janobono.wiwa.business.model.order.OrderItemPartImageData;
import sk.janobono.wiwa.business.model.order.part.PartDuplicatedBasicData;

import java.util.List;

public class PartDuplicatedBasicImageUtil extends BaseImageUtil<PartDuplicatedBasicData> {

    @Override
    public List<OrderItemPartImageData> generateImages(PartDuplicatedBasicData part) {
        return List.of();
    }
}
