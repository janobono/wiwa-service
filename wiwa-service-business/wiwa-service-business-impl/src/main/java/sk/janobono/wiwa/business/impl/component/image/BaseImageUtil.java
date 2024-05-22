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
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;

abstract class BaseImageUtil<P extends PartData> {

    protected static final int FRAME = 75;
    protected static final int FONT_SIZE = 32;
    protected static final int PART_LINE_WIDTH = 12;
    protected static final int EDGE_LINE_WIDTH = 6;
    protected static final int TEXT_MARGIN = 5;

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
        final BufferedImage image = new BufferedImage(x + 2 * FRAME, y + 2 * FRAME, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fill(new Rectangle2D.Double(0, 0, x + 2 * FRAME, y + 2 * FRAME));
        return image;
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
        drawPart(g2d, new DimensionsData(BigDecimal.valueOf(FRAME), BigDecimal.valueOf(FRAME)), dimensions);
    }

    protected void drawPart(final Graphics2D g2d, final DimensionsData p1, final DimensionsData p2) {
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(PART_LINE_WIDTH));
        g2d.draw(new Rectangle2D.Double(p1.x().intValue(), p1.y().intValue(), p2.x().intValue(), p2.y().intValue()));
    }

    protected BigDecimal getTextWidth(final Graphics2D g2d, final String text) {
        final FontMetrics fontMetrics = g2d.getFontMetrics();
        return BigDecimal.valueOf(fontMetrics.stringWidth(text));
    }

    protected BigDecimal getTextHeight(final Graphics2D g2d, final String text) {
        final FontMetrics fontMetrics = g2d.getFontMetrics();
        return BigDecimal.valueOf(getFont().createGlyphVector(fontMetrics.getFontRenderContext(), text).getVisualBounds().getHeight());
    }

    protected DimensionsData getDimensionTextPosition(final Graphics2D g2d,
                                                      final BoardDimension boardDimension,
                                                      final DimensionsData dimensions,
                                                      final String text) {
        final BigDecimal textWidth = getTextWidth(g2d, text);
        final BigDecimal textHeight = getTextHeight(g2d, text);

        return switch (boardDimension) {
            case X -> new DimensionsData(
                    dimensions.x()
                            .add(BigDecimal.valueOf(2 * FRAME))
                            .divide(BigDecimal.TWO, RoundingMode.HALF_UP)
                            .subtract(textWidth.divide(BigDecimal.TWO, RoundingMode.HALF_UP)),
                    textHeight
                            .add(BigDecimal.valueOf(TEXT_MARGIN))
            );
            case Y -> new DimensionsData(
                    textHeight
                            .add(BigDecimal.valueOf(TEXT_MARGIN)),
                    dimensions.y()
                            .add(BigDecimal.valueOf(2 * FRAME))
                            .divide(BigDecimal.TWO, RoundingMode.HALF_UP)
                            .add(textWidth.divide(BigDecimal.TWO, RoundingMode.HALF_UP))
            );
        };
    }

    protected void writeText(final Graphics2D g2d, final String text, final DimensionsData dimensions, final boolean rotate) {
        final AffineTransform defaultAt = g2d.getTransform();
        final int x, y;
        if (rotate) {
            final AffineTransform at = AffineTransform.getQuadrantRotateInstance(3);
            g2d.setTransform(at);
            x = -dimensions.y().intValue();
            y = dimensions.x().intValue();
        } else {
            x = dimensions.x().intValue();
            y = dimensions.y().intValue();
        }
        g2d.drawString(text, x, y);
        g2d.setTransform(defaultAt);
    }

    protected void writeDimension(final Graphics2D g2d,
                                  final BoardDimension boardDimension,
                                  final DimensionsData dimensions,
                                  final OrderPropertiesData orderProperties
    ) {
        final String text = orderProperties.dimensions().getOrDefault(boardDimension, boardDimension.name());
        final DimensionsData textPosition = getDimensionTextPosition(g2d, boardDimension, dimensions, text);
        writeText(g2d, text, textPosition, boardDimension == BoardDimension.Y);
    }

    protected void drawEdge(final Graphics2D g2d,
                            final DimensionsData dimensions,
                            final EdgePosition edgePosition) {
        final int x = dimensions.x().intValue();
        final int y = dimensions.y().intValue();

        final BigDecimal line = BigDecimal.valueOf(PART_LINE_WIDTH).divide(BigDecimal.valueOf(4), RoundingMode.HALF_UP);

        final BigDecimal frame = BigDecimal.valueOf(FRAME - line.intValue());
        final BigDecimal xFrame = BigDecimal.valueOf(x + FRAME + line.intValue());
        final BigDecimal yFrame = BigDecimal.valueOf(y + FRAME + line.intValue());

        final DimensionsData pFrame = new DimensionsData(frame, frame);
        final DimensionsData pXFrame = new DimensionsData(xFrame, frame);
        final DimensionsData pYFrame = new DimensionsData(frame, yFrame);
        final DimensionsData pXYFrame = new DimensionsData(xFrame, yFrame);

        switch (edgePosition) {
            case A1 -> drawEdge(g2d, pFrame, pXFrame);
            case A2 -> drawEdge(g2d, pYFrame, pXYFrame);
            case B1 -> drawEdge(g2d, pFrame, pYFrame);
            case B2 -> drawEdge(g2d, pXFrame, pXYFrame);
        }
    }

    protected void drawEdge(final Graphics2D g2d, final DimensionsData p1, final DimensionsData p2) {
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(EDGE_LINE_WIDTH));
        g2d.draw(new Line2D.Double(
                p1.x().intValue(),
                p1.y().intValue(),
                p2.x().intValue(),
                p2.y().intValue()
        ));
    }

    protected DimensionsData getEdgeTextPosition(final Graphics2D g2d,
                                                 final EdgePosition edgePosition,
                                                 final DimensionsData dimensions,
                                                 final String text) {
        final DimensionsData textXPosition = getDimensionTextPosition(g2d, BoardDimension.X, dimensions, text);
        final DimensionsData textYPosition = getDimensionTextPosition(g2d, BoardDimension.Y, dimensions, text);
        return switch (edgePosition) {
            case A1 -> new DimensionsData(
                    textXPosition.x(),
                    textXPosition.y()
                            .add(getTextHeight(g2d, text))
                            .add(BigDecimal.valueOf(TEXT_MARGIN))
            );
            case A2 -> new DimensionsData(
                    textXPosition.x(),
                    textXPosition.y()
                            .add(BigDecimal.valueOf(FRAME))
                            .add(dimensions.y())
                            .add(getTextHeight(g2d, text))
            );
            case B1 -> new DimensionsData(
                    textYPosition.x()
                            .add(getTextHeight(g2d, text))
                            .add(BigDecimal.valueOf(TEXT_MARGIN)),
                    textYPosition.y()
            );
            case B2 -> new DimensionsData(
                    textYPosition.x()
                            .add(BigDecimal.valueOf(FRAME))
                            .add(dimensions.x())
                            .add(getTextHeight(g2d, text)),
                    textYPosition.y()
            );
            default -> null;
        };
    }

    protected void writeEdge(final Graphics2D g2d,
                             final EdgePosition edgePosition,
                             final DimensionsData dimensions,
                             final OrderPropertiesData orderProperties
    ) {
        writeEdge(g2d, edgePosition, dimensions, orderProperties.edges().getOrDefault(edgePosition, edgePosition.name()));
    }

    protected void writeEdge(final Graphics2D g2d,
                             final EdgePosition edgePosition,
                             final DimensionsData dimensions,
                             final String text
    ) {
        final DimensionsData textPosition = getEdgeTextPosition(g2d, edgePosition, dimensions, text);
        if (textPosition != null) {
            writeEdge(g2d, text, textPosition, switch (edgePosition) {
                case B1, B2 -> true;
                default -> false;
            });
        }
    }

    protected void writeEdge(final Graphics2D g2d, final String text, final DimensionsData textPosition, final boolean rotate) {
        g2d.setColor(Color.RED);
        writeText(g2d, text, textPosition, rotate);
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

    protected DimensionsData getCornerTextPosition(final Graphics2D g2d,
                                                   final CornerPosition cornerPosition,
                                                   final DimensionsData dimensions,
                                                   final String text) {
        return switch (cornerPosition) {
            case A1B1 -> new DimensionsData(
                    BigDecimal.valueOf(FRAME),
                    BigDecimal.valueOf(FRAME)
                            .subtract(getTextHeight(g2d, text))
            );
            case A1B2 -> new DimensionsData(
                    BigDecimal.valueOf(FRAME)
                            .add(dimensions.x())
                            .subtract(getTextWidth(g2d, text)),
                    BigDecimal.valueOf(FRAME)
                            .subtract(getTextHeight(g2d, text))
            );
            case A2B1 -> new DimensionsData(
                    BigDecimal.valueOf(FRAME),
                    BigDecimal.valueOf(FRAME)
                            .add(dimensions.y())
                            .add(getTextHeight(g2d, text))
                            .add(BigDecimal.valueOf(TEXT_MARGIN))
                            .add(BigDecimal.valueOf(PART_LINE_WIDTH))
            );
            case A2B2 -> new DimensionsData(
                    BigDecimal.valueOf(FRAME)
                            .add(dimensions.x())
                            .subtract(getTextWidth(g2d, text)),
                    BigDecimal.valueOf(FRAME)
                            .add(dimensions.y())
                            .add(getTextHeight(g2d, text))
                            .add(BigDecimal.valueOf(TEXT_MARGIN))
                            .add(BigDecimal.valueOf(PART_LINE_WIDTH))
            );
        };
    }

    protected void writeCorner(final Graphics2D g2d,
                               final DimensionsData dimensions,
                               final CornerPosition cornerPosition,
                               final OrderPropertiesData orderProperties) {
        final String text = orderProperties.corners().getOrDefault(cornerPosition, cornerPosition.name());
        final DimensionsData textPosition = getCornerTextPosition(g2d, cornerPosition, dimensions, text);
        if (textPosition != null) {
            g2d.drawString(text, textPosition.x().intValue(), textPosition.y().intValue());
        }
    }

    protected DimensionsData getCornerPosition(final DimensionsData dimensions, final CornerPosition cornerPosition) {
        final BigDecimal line = BigDecimal.valueOf(PART_LINE_WIDTH).divide(BigDecimal.TWO, RoundingMode.HALF_UP);
        final BigDecimal frame = BigDecimal.valueOf(FRAME).subtract(line);
        final BigDecimal frameX = BigDecimal.valueOf(FRAME).add(dimensions.x()).add(line);
        final BigDecimal frameY = BigDecimal.valueOf(FRAME).add(dimensions.y()).add(line);

        return switch (cornerPosition) {
            case A1B1 -> new DimensionsData(frame, frame);
            case A1B2 -> new DimensionsData(frameX, frame);
            case A2B1 -> new DimensionsData(frame, frameY);
            case A2B2 -> new DimensionsData(frameX, frameY);
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

    protected OrderItemImageData generateFullImage(final OrderPropertiesData orderProperties,
                                                   final DimensionsData dimensions,
                                                   final Set<EdgePosition> edges,
                                                   final Map<CornerPosition, PartCornerData> corners) {
        final BufferedImage image = createPartImage(dimensions);
        final Graphics2D g2d = createGraphics(image);
        drawPart(g2d, dimensions);
        writeDimension(g2d, BoardDimension.X, dimensions, orderProperties);
        writeDimension(g2d, BoardDimension.Y, dimensions, orderProperties);
        for (final EdgePosition edgePosition : edges) {
            drawEdge(g2d, dimensions, edgePosition);
            writeEdge(g2d, edgePosition, dimensions, orderProperties);
        }
        corners.forEach((key, value) -> {
            drawCorner(g2d, dimensions, key, value, orderProperties);
        });
        return toOrderItemPartImage(ItemImage.FULL, image);
    }

    protected OrderItemImageData generateSubImage(final ItemImage itemImage,
                                                  final OrderPropertiesData orderProperties,
                                                  final DimensionsData dimensions) {
        final BufferedImage image = createPartImage(dimensions);
        final Graphics2D g2d = createGraphics(image);
        drawPart(g2d, dimensions);
        writeDimension(g2d, BoardDimension.X, dimensions, orderProperties);
        writeDimension(g2d, BoardDimension.Y, dimensions, orderProperties);
        return toOrderItemPartImage(itemImage, image);
    }
}
