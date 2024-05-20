package sk.janobono.wiwa.business.impl.component.image;

import sk.janobono.wiwa.business.model.application.OrderPropertiesData;
import sk.janobono.wiwa.business.model.order.OrderItemImageData;
import sk.janobono.wiwa.business.model.order.part.PartBasicData;
import sk.janobono.wiwa.model.BoardDimension;
import sk.janobono.wiwa.model.EdgePosition;
import sk.janobono.wiwa.model.ItemImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class PartBasicImageUtil extends BaseImageUtil<PartBasicData> {

    @Override
    public List<OrderItemImageData> generateImages(final OrderPropertiesData orderProperties, final PartBasicData part) {
        final BufferedImage image = createPartImage(part.dimensionsTOP());
        final Graphics2D g2d = createGraphics(image);
        drawPart(g2d, part.dimensionsTOP());
        writeDimension(g2d, BoardDimension.X, part.dimensionsTOP(), orderProperties);
        writeDimension(g2d, BoardDimension.Y, part.dimensionsTOP(), orderProperties);
        for (final EdgePosition edgePosition : part.edges().keySet()) {
            drawEdge(g2d, part.dimensionsTOP(), edgePosition, orderProperties);
        }
        part.corners().forEach((key, value) -> {
            drawCorner(g2d, part.dimensionsTOP(), key, value, orderProperties);
        });
        return List.of(toOrderItemPartImage(ItemImage.FULL, image));
    }
}
