package sk.janobono.wiwa.business.impl.component.image;

import sk.janobono.wiwa.business.model.application.OrderPropertiesData;
import sk.janobono.wiwa.business.model.order.OrderItemImageData;
import sk.janobono.wiwa.business.model.order.part.PartDuplicatedFrameData;
import sk.janobono.wiwa.model.ItemImage;

import java.util.ArrayList;
import java.util.List;

public class PartDuplicatedFrameImageUtil extends BaseImageUtil<PartDuplicatedFrameData> {

    @Override
    public List<OrderItemImageData> generateImages(final OrderPropertiesData orderProperties, final PartDuplicatedFrameData part) {
        final List<OrderItemImageData> images = new ArrayList<>();
        images.add(generateFullImage(orderProperties, part.dimensionsTOP(), part.edges().keySet(), part.corners()));
        images.add(generateSubImage(ItemImage.TOP, orderProperties, part.dimensionsTOP()));
        if (part.dimensionsA1() != null) {
            images.add(generateSubImage(ItemImage.A1, orderProperties, part.dimensionsA1()));
        }
        if (part.dimensionsA2() != null) {
            images.add(generateSubImage(ItemImage.A2, orderProperties, part.dimensionsA2()));
        }
        if (part.dimensionsB1() != null) {
            images.add(generateSubImage(ItemImage.B1, orderProperties, part.dimensionsB1()));
        }
        if (part.dimensionsB2() != null) {
            images.add(generateSubImage(ItemImage.B2, orderProperties, part.dimensionsB2()));
        }
        return images;
    }
}
