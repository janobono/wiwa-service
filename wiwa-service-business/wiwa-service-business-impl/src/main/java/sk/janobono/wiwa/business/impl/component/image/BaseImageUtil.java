package sk.janobono.wiwa.business.impl.component.image;

import org.springframework.http.MediaType;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.OrderPropertiesData;
import sk.janobono.wiwa.business.model.order.OrderItemImageData;
import sk.janobono.wiwa.business.model.order.part.PartCornerData;
import sk.janobono.wiwa.business.model.order.part.PartCornerRoundedData;
import sk.janobono.wiwa.business.model.order.part.PartCornerStraightData;
import sk.janobono.wiwa.business.model.order.part.PartData;
import sk.janobono.wiwa.model.BoardDimension;
import sk.janobono.wiwa.model.CornerPosition;
import sk.janobono.wiwa.model.EdgePosition;
import sk.janobono.wiwa.model.ItemImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

abstract class BaseImageUtil<P extends PartData> {

    protected static final int FRAME = 100;
    protected static final int FONT_SIZE = 32;
    protected static final int PART_LINE_WIDTH = 12;
    protected static final int EDGE_LINE_WIDTH = 6;

    public abstract List<OrderItemImageData> generateImages(final OrderPropertiesData orderProperties, final P part);

    protected OrderItemImageData toOrderItemPartImage(final ItemImage itemImage, final BufferedImage bufferedImage) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return new OrderItemImageData(
                itemImage,
                MediaType.IMAGE_PNG_VALUE,
                byteArrayOutputStream.toByteArray()
        );
    }

    protected Font getFont() {
        return new Font(Font.MONOSPACED, Font.PLAIN, FONT_SIZE);
    }

    protected BufferedImage createPartImage(final DimensionsData dimensions) {
        final int x = dimensions.x().intValue();
        final int y = dimensions.y().intValue();
        return new BufferedImage(x + 2 * FRAME, y + 2 * FRAME, BufferedImage.TYPE_INT_ARGB);
    }

    protected Graphics2D createGraphics(final BufferedImage image) {
        final Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(getFont());
        return g2d;
    }

    protected void drawPart(final Graphics2D g2d, final DimensionsData dimensions) {
        final int x = dimensions.x().intValue();
        final int y = dimensions.y().intValue();

        g2d.setColor(Color.WHITE);
        g2d.fill(new Rectangle2D.Double(0, 0, x + 2 * FRAME, y + 2 * FRAME));

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(PART_LINE_WIDTH));
        g2d.draw(new Rectangle2D.Double(FRAME, FRAME, x, y));
    }

    protected void writeDimension(final Graphics2D g2d,
                                  final BoardDimension boardDimension,
                                  final DimensionsData dimensions,
                                  final OrderPropertiesData orderProperties
    ) {
        final String text = orderProperties.dimensions().getOrDefault(boardDimension, boardDimension.name());
        final FontMetrics fontMetrics = g2d.getFontMetrics();
        final BigDecimal textWidth = BigDecimal.valueOf(fontMetrics.stringWidth(text));
        final BigDecimal ascent = BigDecimal.valueOf(fontMetrics.getAscent());

        final DimensionsData textPosition = switch (boardDimension) {
            case X -> new DimensionsData(
                    dimensions.x()
                            .add(BigDecimal.valueOf(2 * FRAME))
                            .divide(BigDecimal.TWO, RoundingMode.HALF_UP)
                            .subtract(textWidth.divide(BigDecimal.TWO, RoundingMode.HALF_UP)),
                    BigDecimal.valueOf(fontMetrics.getMaxAscent())
            );
            case Y -> new DimensionsData(
                    textWidth.divide(BigDecimal.TWO, RoundingMode.HALF_UP),
                    dimensions.y()
                            .add(BigDecimal.valueOf(2 * FRAME))
                            .divide(BigDecimal.TWO, RoundingMode.HALF_UP)
                            .add(ascent.divide(BigDecimal.TWO, RoundingMode.HALF_UP))
            );
        };

        g2d.drawString(text, textPosition.x().intValue(), textPosition.y().intValue());
    }

    protected void drawEdge(final Graphics2D g2d,
                            final DimensionsData dimensions,
                            final EdgePosition edgePosition,
                            final OrderPropertiesData orderProperties) {
        final int x = dimensions.x().intValue();
        final int y = dimensions.y().intValue();

        final BigDecimal line = BigDecimal.valueOf(PART_LINE_WIDTH).divide(BigDecimal.valueOf(4), RoundingMode.HALF_UP);

        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(EDGE_LINE_WIDTH));

        switch (edgePosition) {
            case A1 -> g2d.draw(new Line2D.Double(
                    FRAME - line.intValue(),
                    FRAME - line.intValue(),
                    x + FRAME + line.intValue(),
                    FRAME - line.intValue())
            );
            case A2 -> g2d.draw(new Line2D.Double(
                    FRAME - line.intValue(),
                    y + FRAME + line.intValue(),
                    x + FRAME + line.intValue(),
                    y + FRAME + line.intValue())
            );
            case B1 -> g2d.draw(new Line2D.Double(
                    FRAME - line.intValue(),
                    FRAME - line.intValue(),
                    FRAME - line.intValue(),
                    y + FRAME + line.intValue())
            );
            case B2 -> g2d.draw(new Line2D.Double(
                    x + FRAME + line.intValue(),
                    FRAME - line.intValue(),
                    x + FRAME + line.intValue(),
                    y + FRAME + line.intValue())
            );
        }
        writeEdge(g2d, edgePosition, dimensions, orderProperties);
    }

    protected void writeEdge(final Graphics2D g2d,
                             final EdgePosition edgePosition,
                             final DimensionsData dimensions,
                             final OrderPropertiesData orderProperties
    ) {
        final String text = orderProperties.edges().getOrDefault(edgePosition, edgePosition.name());
        final FontMetrics fontMetrics = g2d.getFontMetrics();
        final BigDecimal textWidth = BigDecimal.valueOf(fontMetrics.stringWidth(text));
        final BigDecimal ascent = BigDecimal.valueOf(fontMetrics.getAscent());

        final DimensionsData textPosition = switch (edgePosition) {
            case A1 -> new DimensionsData(
                    dimensions.x()
                            .add(BigDecimal.valueOf(2 * FRAME))
                            .divide(BigDecimal.TWO, RoundingMode.HALF_UP)
                            .subtract(textWidth.divide(BigDecimal.TWO, RoundingMode.HALF_UP)),
                    BigDecimal.valueOf(FRAME)
                            .subtract(ascent.divide(BigDecimal.TWO, RoundingMode.HALF_UP))
            );
            case A2 -> new DimensionsData(
                    dimensions.x()
                            .add(BigDecimal.valueOf(2 * FRAME))
                            .divide(BigDecimal.TWO, RoundingMode.HALF_UP)
                            .subtract(textWidth.divide(BigDecimal.TWO, RoundingMode.HALF_UP)),
                    BigDecimal.valueOf(FRAME)
                            .add(dimensions.y())
                            .add(BigDecimal.valueOf(fontMetrics.getHeight()))
            );
            case B1 -> new DimensionsData(
                    BigDecimal.valueOf(FRAME)
                            .divide(BigDecimal.TWO, RoundingMode.HALF_UP),
                    dimensions.y()
                            .add(BigDecimal.valueOf(2 * FRAME))
                            .divide(BigDecimal.TWO, RoundingMode.HALF_UP)
                            .add(ascent.divide(BigDecimal.TWO, RoundingMode.HALF_UP))
            );
            case B2 -> new DimensionsData(
                    dimensions.x()
                            .add(BigDecimal.valueOf(FRAME))
                            .add(textWidth.divide(BigDecimal.TWO, RoundingMode.HALF_UP)),
                    dimensions.y()
                            .add(BigDecimal.valueOf(2 * FRAME))
                            .divide(BigDecimal.TWO, RoundingMode.HALF_UP)
                            .add(ascent.divide(BigDecimal.TWO, RoundingMode.HALF_UP))
            );
            default -> null;
        };

        if (textPosition != null) {
            g2d.drawString(text, textPosition.x().intValue(), textPosition.y().intValue());
        }
    }

    protected void drawCorner(final Graphics2D g2d,
                              final DimensionsData dimensions,
                              final CornerPosition cornerPosition,
                              final PartCornerData corner,
                              final OrderPropertiesData orderProperties) {
        final DimensionsData cornerPositionDimensions = getCornerPosition(dimensions, cornerPosition);
        clearCorner(g2d, cornerPosition, cornerPositionDimensions, corner.dimensions());

        switch (corner) {
            case final PartCornerStraightData cornerStraight ->
                    drawCornerStraight(g2d, cornerPosition, cornerStraight, cornerPositionDimensions);
            case final PartCornerRoundedData cornerRounded ->
                    drawCornerRounded(g2d, cornerPosition, cornerRounded, cornerPositionDimensions);
            default -> {
            }
        }
        g2d.setColor(Color.RED);
        writeCorner(g2d, dimensions, cornerPosition, orderProperties);
    }

    protected void writeCorner(final Graphics2D g2d,
                               final DimensionsData dimensions,
                               final CornerPosition cornerPosition,
                               final OrderPropertiesData orderProperties) {
        final String text = orderProperties.corners().getOrDefault(cornerPosition, cornerPosition.name());
        final FontMetrics fontMetrics = g2d.getFontMetrics();
        final BigDecimal textWidth = BigDecimal.valueOf(fontMetrics.stringWidth(text));
        final BigDecimal ascent = BigDecimal.valueOf(fontMetrics.getAscent());

        final BigDecimal frameTextX = BigDecimal.valueOf(FRAME).subtract(textWidth.divide(BigDecimal.TWO, RoundingMode.HALF_UP));
        final BigDecimal frameTextY = BigDecimal.valueOf(FRAME).subtract(ascent.divide(BigDecimal.TWO, RoundingMode.HALF_UP));

        final DimensionsData textPosition = switch (cornerPosition) {
            case A1B1 -> new DimensionsData(
                    frameTextX,
                    frameTextY
            );
            case A1B2 -> new DimensionsData(
                    dimensions.x().add(textWidth),
                    frameTextY
            );
            case A2B1 -> new DimensionsData(
                    frameTextX,
                    BigDecimal.valueOf(FRAME)
                            .add(dimensions.y())
                            .add(BigDecimal.valueOf(fontMetrics.getHeight()))
            );
            case A2B2 -> new DimensionsData(
                    dimensions.x().add(textWidth),
                    BigDecimal.valueOf(FRAME)
                            .add(dimensions.y())
                            .add(BigDecimal.valueOf(fontMetrics.getHeight()))
            );
            default -> null;
        };

        if (textPosition != null) {
            g2d.drawString(text, textPosition.x().intValue(), textPosition.y().intValue());
        }
    }

    protected DimensionsData getCornerPosition(final DimensionsData dimensions,
                                               final CornerPosition cornerPosition) {
        final BigDecimal line = BigDecimal.valueOf(PART_LINE_WIDTH).divide(BigDecimal.TWO, RoundingMode.HALF_UP);
        return switch (cornerPosition) {
            case A1B1 -> new DimensionsData(
                    BigDecimal.valueOf(FRAME)
                            .subtract(line),
                    BigDecimal.valueOf(FRAME)
                            .subtract(line)
            );
            case A1B2 -> new DimensionsData(
                    BigDecimal.valueOf(FRAME)
                            .add(dimensions.x())
                            .add(line),
                    BigDecimal.valueOf(FRAME)
                            .subtract(line)
            );
            case A2B1 -> new DimensionsData(
                    BigDecimal.valueOf(FRAME)
                            .subtract(line),
                    BigDecimal.valueOf(FRAME)
                            .add(dimensions.y())
                            .add(line)
            );
            case A2B2 -> new DimensionsData(
                    BigDecimal.valueOf(FRAME)
                            .add(dimensions.x())
                            .add(line),
                    BigDecimal.valueOf(FRAME)
                            .add(dimensions.y())
                            .add(line)
            );
        };
    }

    protected void clearCorner(final Graphics2D g2d,
                               final CornerPosition cornerPosition,
                               final DimensionsData cornerPositionDimensions,
                               final DimensionsData cornerDimensions) {
        g2d.setColor(Color.WHITE);
        switch (cornerPosition) {
            case A1B1 -> g2d.fillRect(
                    cornerPositionDimensions.x().intValue(), cornerPositionDimensions.y().intValue(),
                    cornerDimensions.x().intValue(), cornerDimensions.y().intValue());
            case A1B2 -> g2d.fillRect(
                    cornerPositionDimensions.x().subtract(cornerDimensions.x()).intValue(), cornerPositionDimensions.y().intValue(),
                    cornerDimensions.x().intValue(), cornerDimensions.y().intValue());
            case A2B1 -> g2d.fillRect(
                    cornerPositionDimensions.x().intValue(), cornerPositionDimensions.y().subtract(cornerDimensions.y()).intValue(),
                    cornerDimensions.x().intValue(), cornerDimensions.y().intValue());
            case A2B2 -> g2d.fillRect(
                    cornerPositionDimensions.x().subtract(cornerDimensions.x()).intValue(), cornerPositionDimensions.y().subtract(cornerDimensions.y()).intValue(),
                    cornerDimensions.x().intValue(), cornerDimensions.y().intValue());
        }
    }

    protected void drawCornerStraight(final Graphics2D g2d,
                                      final CornerPosition cornerPosition,
                                      final PartCornerStraightData cornerStraight,
                                      final DimensionsData cornerPositionDimensions) {
        final BigDecimal line = BigDecimal.valueOf(PART_LINE_WIDTH).divide(BigDecimal.valueOf(1.5), RoundingMode.HALF_UP);
        final BigDecimal edge = BigDecimal.valueOf(PART_LINE_WIDTH).divide(BigDecimal.valueOf(3), RoundingMode.HALF_UP);
        switch (cornerPosition) {
            case A1B1 -> {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(PART_LINE_WIDTH));
                g2d.drawLine(
                        cornerPositionDimensions.x().add(line).intValue(),
                        cornerPositionDimensions.y().add(cornerStraight.dimensions().y()).intValue(),
                        cornerPositionDimensions.x().add(cornerStraight.dimensions().x()).intValue(),
                        cornerPositionDimensions.y().add(line).intValue()
                );

                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(EDGE_LINE_WIDTH));
                g2d.drawLine(
                        cornerPositionDimensions.x().add(edge).intValue(),
                        cornerPositionDimensions.y().add(cornerStraight.dimensions().y()).intValue(),
                        cornerPositionDimensions.x().add(cornerStraight.dimensions().x()).intValue(),
                        cornerPositionDimensions.y().add(edge).intValue()
                );
            }
            case A1B2 -> {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(PART_LINE_WIDTH));
                g2d.drawLine(
                        cornerPositionDimensions.x().subtract(cornerStraight.dimensions().x()).intValue(),
                        cornerPositionDimensions.y().add(line).intValue(),
                        cornerPositionDimensions.x().subtract(line).intValue(),
                        cornerPositionDimensions.y().add(cornerStraight.dimensions().y()).intValue()
                );

                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(EDGE_LINE_WIDTH));
                g2d.drawLine(
                        cornerPositionDimensions.x().subtract(cornerStraight.dimensions().x()).intValue(),
                        cornerPositionDimensions.y().add(edge).intValue(),
                        cornerPositionDimensions.x().subtract(edge).intValue(),
                        cornerPositionDimensions.y().add(cornerStraight.dimensions().y()).intValue()
                );
            }
            case A2B1 -> {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(PART_LINE_WIDTH));
                g2d.drawLine(
                        cornerPositionDimensions.x().add(line).intValue(),
                        cornerPositionDimensions.y().subtract(cornerStraight.dimensions().y()).intValue(),
                        cornerPositionDimensions.x().add(cornerStraight.dimensions().x()).intValue(),
                        cornerPositionDimensions.y().subtract(line).intValue()
                );

                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(EDGE_LINE_WIDTH));
                g2d.drawLine(
                        cornerPositionDimensions.x().add(edge).intValue(),
                        cornerPositionDimensions.y().subtract(cornerStraight.dimensions().y()).intValue(),
                        cornerPositionDimensions.x().add(cornerStraight.dimensions().x()).intValue(),
                        cornerPositionDimensions.y().subtract(edge).intValue()
                );
            }
            case A2B2 -> {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(PART_LINE_WIDTH));
                g2d.drawLine(
                        cornerPositionDimensions.x().subtract(cornerStraight.dimensions().x()).intValue(),
                        cornerPositionDimensions.y().subtract(line).intValue(),
                        cornerPositionDimensions.x().subtract(line).intValue(),
                        cornerPositionDimensions.y().subtract(cornerStraight.dimensions().y()).intValue()
                );

                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(EDGE_LINE_WIDTH));
                g2d.drawLine(
                        cornerPositionDimensions.x().subtract(cornerStraight.dimensions().x()).intValue(),
                        cornerPositionDimensions.y().subtract(edge).intValue(),
                        cornerPositionDimensions.x().subtract(edge).intValue(),
                        cornerPositionDimensions.y().subtract(cornerStraight.dimensions().y()).intValue()
                );
            }
        }
    }

    protected void drawCornerRounded(final Graphics2D g2d,
                                     final CornerPosition cornerPosition,
                                     final PartCornerRoundedData cornerRounded,
                                     final DimensionsData cornerPositionDimensions) {
        final BigDecimal line = BigDecimal.valueOf(PART_LINE_WIDTH).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
        final BigDecimal edge = BigDecimal.valueOf(PART_LINE_WIDTH).divide(BigDecimal.valueOf(3), RoundingMode.HALF_UP);
        switch (cornerPosition) {
            case A1B1 -> {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(PART_LINE_WIDTH));
                g2d.drawArc(
                        cornerPositionDimensions.x().add(line).intValue(),
                        cornerPositionDimensions.y().add(line).intValue(),
                        cornerRounded.radius().multiply(BigDecimal.valueOf(2)).intValue(),
                        cornerRounded.radius().multiply(BigDecimal.valueOf(2)).intValue(),
                        90,
                        90);

                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(EDGE_LINE_WIDTH));
                g2d.drawArc(
                        cornerPositionDimensions.x().add(edge).intValue(),
                        cornerPositionDimensions.y().add(edge).intValue(),
                        cornerRounded.radius().multiply(BigDecimal.valueOf(2)).intValue(),
                        cornerRounded.radius().multiply(BigDecimal.valueOf(2)).intValue(),
                        90,
                        90);
            }
            case A1B2 -> {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(PART_LINE_WIDTH));
                g2d.drawArc(
                        cornerPositionDimensions.x()
                                .subtract(line)
                                .subtract(cornerRounded.radius().multiply(BigDecimal.valueOf(2)))
                                .intValue(),
                        cornerPositionDimensions.y()
                                .add(line)
                                .intValue(),
                        cornerRounded.radius().multiply(BigDecimal.valueOf(2)).intValue(),
                        cornerRounded.radius().multiply(BigDecimal.valueOf(2)).intValue(),
                        0,
                        90);

                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(EDGE_LINE_WIDTH));
                g2d.drawArc(
                        cornerPositionDimensions.x()
                                .subtract(edge)
                                .subtract(cornerRounded.radius().multiply(BigDecimal.valueOf(2)))
                                .intValue(),
                        cornerPositionDimensions.y()
                                .add(edge)
                                .intValue(),
                        cornerRounded.radius().multiply(BigDecimal.valueOf(2)).intValue(),
                        cornerRounded.radius().multiply(BigDecimal.valueOf(2)).intValue(),
                        0,
                        90);
            }
            case A2B1 -> {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(PART_LINE_WIDTH));
                g2d.drawArc(
                        cornerPositionDimensions.x()
                                .add(line)
                                .intValue(),
                        cornerPositionDimensions.y()
                                .subtract(line)
                                .subtract(cornerRounded.radius().multiply(BigDecimal.valueOf(2)))
                                .intValue(),
                        cornerRounded.radius().multiply(BigDecimal.valueOf(2)).intValue(),
                        cornerRounded.radius().multiply(BigDecimal.valueOf(2)).intValue(),
                        180,
                        90);

                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(EDGE_LINE_WIDTH));
                g2d.drawArc(
                        cornerPositionDimensions.x()
                                .add(edge)
                                .intValue(),
                        cornerPositionDimensions.y()
                                .subtract(edge)
                                .subtract(cornerRounded.radius().multiply(BigDecimal.valueOf(2)))
                                .intValue(),
                        cornerRounded.radius().multiply(BigDecimal.valueOf(2)).intValue(),
                        cornerRounded.radius().multiply(BigDecimal.valueOf(2)).intValue(),
                        180,
                        90);
            }
            case A2B2 -> {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(PART_LINE_WIDTH));
                drawRounded(g2d, cornerRounded, cornerPositionDimensions, line);

                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(EDGE_LINE_WIDTH));
                drawRounded(g2d, cornerRounded, cornerPositionDimensions, edge);
            }
        }
    }

    private void drawRounded(final Graphics2D g2d,
                             final PartCornerRoundedData cornerRounded,
                             final DimensionsData cornerPositionDimensions,
                             final BigDecimal line) {
        g2d.drawArc(
                cornerPositionDimensions.x()
                        .subtract(cornerRounded.radius().multiply(BigDecimal.valueOf(2)))
                        .subtract(line)
                        .intValue(),
                cornerPositionDimensions.y()
                        .subtract(line)
                        .subtract(cornerRounded.radius().multiply(BigDecimal.valueOf(2)))
                        .intValue(),
                cornerRounded.radius().multiply(BigDecimal.valueOf(2)).intValue(),
                cornerRounded.radius().multiply(BigDecimal.valueOf(2)).intValue(),
                270,
                90);
    }
}
