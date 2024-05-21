package sk.janobono.wiwa.business.impl.component.image;

import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.OrderPropertiesData;
import sk.janobono.wiwa.business.model.order.OrderItemImageData;
import sk.janobono.wiwa.business.model.order.part.PartFrameData;
import sk.janobono.wiwa.model.BoardDimension;
import sk.janobono.wiwa.model.EdgePosition;
import sk.janobono.wiwa.model.FrameType;
import sk.janobono.wiwa.model.ItemImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PartFrameImageUtil extends BaseImageUtil<PartFrameData> {

    @Override
    public List<OrderItemImageData> generateImages(final OrderPropertiesData orderProperties, final PartFrameData part) {
        return List.of(
                generateFullImage(orderProperties, part),
                generateSubImage(ItemImage.A1, orderProperties, part.dimensionsA1()),
                generateSubImage(ItemImage.A2, orderProperties, part.dimensionsA2()),
                generateSubImage(ItemImage.B1, orderProperties, part.dimensionsB1()),
                generateSubImage(ItemImage.B2, orderProperties, part.dimensionsB2())
        );
    }

    private OrderItemImageData generateFullImage(final OrderPropertiesData orderProperties, final PartFrameData part) {
        final BufferedImage image = createPartImage(part.dimensionsTOP());
        final Graphics2D g2d = createGraphics(image);
        if (part.frameType() == FrameType.VERTICAL) {
            drawVertical(part, g2d);
        } else {
            drawHorizontal(part, g2d);
        }

        writeDimension(g2d, BoardDimension.X, part.dimensionsTOP(), orderProperties);
        writeDimension(g2d, BoardDimension.Y, part.dimensionsTOP(), orderProperties);

        for (final EdgePosition edgePosition : part.edges().keySet()) {
            drawEdge(g2d, part.dimensionsTOP(), edgePosition, orderProperties);
        }

        final BigDecimal line = BigDecimal.valueOf(PART_LINE_WIDTH).divide(BigDecimal.valueOf(4), RoundingMode.HALF_UP);

        return toOrderItemPartImage(ItemImage.FULL, image);
    }

    private void drawHorizontal(final PartFrameData part, final Graphics2D g2d) {
        drawPart(g2d, part.dimensionsA1());
        drawPart(g2d,
                new DimensionsData(BigDecimal.valueOf(FRAME),
                        BigDecimal.valueOf(FRAME)
                                .add(part.dimensionsA1().y())
                                .add(part.dimensionsB1().y())
                ),
                part.dimensionsA2()
        );
        drawPart(g2d,
                new DimensionsData(
                        BigDecimal.valueOf(FRAME),
                        BigDecimal.valueOf(FRAME)
                                .add(part.dimensionsA1().y())
                ),
                part.dimensionsB1()
        );
        drawPart(g2d,
                new DimensionsData(
                        BigDecimal.valueOf(FRAME)
                                .add(part.dimensionsA1().x())
                                .subtract(part.dimensionsB2().x()),
                        BigDecimal.valueOf(FRAME)
                                .add(part.dimensionsA1().y())
                ),
                part.dimensionsB2()
        );
    }

    private void drawVertical(final PartFrameData part, final Graphics2D g2d) {
        drawPart(g2d,
                new DimensionsData(
                        BigDecimal.valueOf(FRAME)
                                .add(part.dimensionsB1().x()),
                        BigDecimal.valueOf(FRAME)
                ),
                part.dimensionsA1());
        drawPart(g2d,
                new DimensionsData(
                        BigDecimal.valueOf(FRAME)
                                .add(part.dimensionsB1().x()),
                        BigDecimal.valueOf(FRAME)
                                .add(part.dimensionsB1().y())
                                .subtract(part.dimensionsA2().y())
                ),
                part.dimensionsA2()
        );
        drawPart(g2d, part.dimensionsB1());
        drawPart(g2d,
                new DimensionsData(
                        BigDecimal.valueOf(FRAME)
                                .add(part.dimensionsB1().x())
                                .add(part.dimensionsA1().x()),
                        BigDecimal.valueOf(FRAME)
                ),
                part.dimensionsB2()
        );
    }
}
