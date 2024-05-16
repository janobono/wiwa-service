package sk.janobono.wiwa.business.impl.component.image;

import sk.janobono.wiwa.business.model.order.OrderItemPartImageData;
import sk.janobono.wiwa.business.model.order.part.PartDuplicatedFrameData;

import java.util.List;

public class PartDuplicatedFrameImageUtil extends BaseImageUtil<PartDuplicatedFrameData> {

    @Override
    public List<OrderItemPartImageData> generateImages(PartDuplicatedFrameData part) {
        return List.of();
    }
}
