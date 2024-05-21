package sk.janobono.wiwa.business.impl.component.image;

import sk.janobono.wiwa.business.model.application.OrderPropertiesData;
import sk.janobono.wiwa.business.model.order.OrderItemImageData;
import sk.janobono.wiwa.business.model.order.part.PartBasicData;
import sk.janobono.wiwa.model.ItemImage;

import java.util.List;

public class PartBasicImageUtil extends BaseImageUtil<PartBasicData> {

    @Override
    public List<OrderItemImageData> generateImages(final OrderPropertiesData orderProperties, final PartBasicData part) {
        return List.of(
                generateFullImage(orderProperties, part.dimensionsTOP(), part.edges().keySet(), part.corners()),
                generateSubImage(ItemImage.TOP, orderProperties, part.dimensionsTOP())
        );
    }
}
