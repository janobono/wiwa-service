package sk.janobono.wiwa.business.impl.component.image;

import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.OrderPropertiesData;
import sk.janobono.wiwa.business.model.order.OrderItemImageData;
import sk.janobono.wiwa.business.model.order.part.PartDuplicatedFrameData;
import sk.janobono.wiwa.model.BoardDimension;
import sk.janobono.wiwa.model.EdgePosition;
import sk.janobono.wiwa.model.ItemImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PartDuplicatedFrameImageUtil extends BaseImageUtil<PartDuplicatedFrameData> {

    @Override
    public List<OrderItemImageData> generateImages(final OrderPropertiesData orderProperties, final PartDuplicatedFrameData part) {
        final List<OrderItemImageData> images = new ArrayList<>();
        images.add(generateFullImage(orderProperties, part.dimensionsTOP(), part.edges().keySet(), part.corners()));
        images.add(generateSubImage(ItemImage.TOP, orderProperties, part.dimensionsTOP()));
        if (part.dimensionsA1() != null) {
            images.add(generateSubImage(ItemImage.A1, orderProperties, part.dimensionsA1(), part.edgeIdA1I() != null));
        }
        if (part.dimensionsA2() != null) {
            images.add(generateSubImage(ItemImage.A2, orderProperties, part.dimensionsA2(), part.edgeIdA2I() != null));
        }
        if (part.dimensionsB1() != null) {
            images.add(generateSubImage(ItemImage.B1, orderProperties, part.dimensionsB1(), part.edgeIdB1I() != null));
        }
        if (part.dimensionsB2() != null) {
            images.add(generateSubImage(ItemImage.B2, orderProperties, part.dimensionsB2(), part.edgeIdB2I() != null));
        }
        return images;
    }

    protected OrderItemImageData generateSubImage(final ItemImage itemImage,
                                                  final OrderPropertiesData orderProperties,
                                                  final DimensionsData dimensions,
                                                  final boolean internalEdge) {
        final BufferedImage image = createPartImage(dimensions);
        final Graphics2D g2d = createGraphics(image);
        drawPart(g2d, dimensions);
        writeDimension(g2d, BoardDimension.X, dimensions, orderProperties);
        writeDimension(g2d, BoardDimension.Y, dimensions, orderProperties);

        if (internalEdge) {
            switch (itemImage) {
                case A1 -> {
                    drawEdge(g2d, dimensions, EdgePosition.A2);
                    writeEdge(g2d, EdgePosition.A2, dimensions, orderProperties.edges().getOrDefault(EdgePosition.A1I, EdgePosition.A1I.name()));
                }
                case A2 -> {
                    drawEdge(g2d, dimensions, EdgePosition.A1);
                    writeEdge(g2d, EdgePosition.A1, dimensions, orderProperties.edges().getOrDefault(EdgePosition.A2I, EdgePosition.A2I.name()));
                }
                case B1 -> {
                    drawEdge(g2d, dimensions, EdgePosition.B2);
                    writeEdge(g2d, EdgePosition.B2, dimensions, orderProperties.edges().getOrDefault(EdgePosition.B1I, EdgePosition.B1I.name()));
                }
                case B2 -> {
                    drawEdge(g2d, dimensions, EdgePosition.B1);
                    writeEdge(g2d, EdgePosition.B1, dimensions, orderProperties.edges().getOrDefault(EdgePosition.B2I, EdgePosition.B2I.name()));
                }
            }

        }
        return toOrderItemPartImage(itemImage, image);
    }
}
