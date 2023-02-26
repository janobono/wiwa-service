package sk.janobono.wiwa.common.component;

import org.springframework.stereotype.Component;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Base64;

@Component
public class ImageUtil {

    public byte[] scaleImage(String fileType, byte[] data, int maxWidth, int maxHeight) {
        try {
            return toByteArray(scaleImage(toBufferedImage(data), maxWidth, maxHeight), formatName(fileType));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] scaleImageAndCropToSquare(String fileType, byte[] data, int maxSize) {
        try {
            return toByteArray(scaleImageAndCropToSquare(toBufferedImage(data), maxSize), formatName(fileType));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] generateMessageImage(String message) {
        if (message == null || message.isBlank()) {
            message = "NOT FOUND";
        }

        Font font = new Font("Arial", Font.PLAIN, 18);

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(message.toUpperCase());

        int height = fm.getHeight();
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(Color.BLACK);
        g2d.drawString(message.toUpperCase(), 0, fm.getAscent());
        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }

    private String formatName(String fileType) {
        return fileType.replaceFirst("image/", "");
    }

    private byte[] toByteArray(BufferedImage bufferedImage, String formatName) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            switch (formatName.toLowerCase()) {
                case "jpeg", "jpg" -> processJPEGOutput(byteArrayOutputStream, bufferedImage, formatName);
                case "png" -> processPNGOutput(byteArrayOutputStream, bufferedImage, formatName);
                default -> // unsupported image type will be processed without default compression and DPI modification
                        ImageIO.write(bufferedImage, formatName, byteArrayOutputStream);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }

    private BufferedImage toBufferedImage(byte[] data) {
        try (InputStream is = new ByteArrayInputStream(data)) {
            return ImageIO.read(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedImage scaleImageAndCropToSquare(BufferedImage image, int maxSize) {
        int height = image.getHeight();
        int width = image.getWidth();
        int side = Math.min(width, height);

        int x = (image.getWidth() - side) / 2;
        int y = (image.getHeight() - side) / 2;

        BufferedImage cropped = image.getSubimage(x, y, side, side);
        Image scaledImg = cropped.getScaledInstance(maxSize, maxSize, Image.SCALE_SMOOTH);
        BufferedImage scaled = new BufferedImage(scaledImg.getWidth(null), scaledImg.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics graphics = scaled.getGraphics();
        graphics.drawImage(scaledImg, 0, 0, maxSize, maxSize, null);
        graphics.dispose();
        return scaled;
    }

    private BufferedImage scaleImage(BufferedImage image, int maxWidth, int maxHeight) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        float horizontalRatio = 1;
        float verticalRatio = 1;
        if (imageHeight > maxHeight) {
            verticalRatio = (float) maxHeight / (float) imageHeight;
        }
        if (imageWidth > maxWidth) {
            horizontalRatio = (float) maxWidth / (float) imageWidth;
        }
        float scaleRatio = 1;
        if (verticalRatio < horizontalRatio) {
            scaleRatio = verticalRatio;
        } else if (horizontalRatio < verticalRatio) {
            scaleRatio = horizontalRatio;
        }
        int destWidth = (int) (imageWidth * scaleRatio);
        int destHeight = (int) (imageHeight * scaleRatio);

        Image scaledImg = image.getScaledInstance(destWidth, destHeight, Image.SCALE_SMOOTH);
        BufferedImage scaled = new BufferedImage(destWidth, destHeight, image.getType());
        Graphics graphics = scaled.getGraphics();
        graphics.drawImage(scaledImg, 0, 0, destWidth, destHeight, null);
        graphics.dispose();
        return scaled;
    }

    private void processJPEGOutput(ByteArrayOutputStream byteArrayOutputStream, BufferedImage bufferedImage, String formatName) throws IOException {
        ImageWriter imageWriter = ImageIO.getImageWritersByFormatName(formatName).next();
        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionQuality(0.90f);
        ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
        IIOMetadata metadata = imageWriter.getDefaultImageMetadata(typeSpecifier, imageWriteParam);

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(byteArrayOutputStream)) {
            if (!metadata.isReadOnly() && metadata.isStandardMetadataFormatSupported()) {
                IIOMetadataNode root = new IIOMetadataNode("javax_imageio_jpeg_image_1.0");
                IIOMetadataNode jpegVariety = new IIOMetadataNode("JPEGvariety");
                IIOMetadataNode markerSequence = new IIOMetadataNode("markerSequence");
                IIOMetadataNode app0JFIF = new IIOMetadataNode("app0JFIF");

                app0JFIF.setAttribute("majorVersion", "1");
                app0JFIF.setAttribute("minorVersion", "2");
                app0JFIF.setAttribute("thumbWidth", "0");
                app0JFIF.setAttribute("thumbHeight", "0");
                app0JFIF.setAttribute("resUnits", "01");
                app0JFIF.setAttribute("Xdensity", String.valueOf(96));
                app0JFIF.setAttribute("Ydensity", String.valueOf(96));

                root.appendChild(jpegVariety);
                root.appendChild(markerSequence);
                jpegVariety.appendChild(app0JFIF);

                metadata.mergeTree("javax_imageio_jpeg_image_1.0", root);
            }
            imageWriter.setOutput(ios);
            imageWriter.write(metadata, new IIOImage(bufferedImage, null, metadata), imageWriteParam);
        }
    }

    private void processPNGOutput(ByteArrayOutputStream byteArrayOutputStream, BufferedImage bufferedImage, String formatName) throws IOException {
        ImageWriter imageWriter = ImageIO.getImageWritersByFormatName(formatName).next();
        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
        IIOMetadata metadata = imageWriter.getDefaultImageMetadata(typeSpecifier, imageWriteParam);

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(byteArrayOutputStream)) {
            if (!metadata.isReadOnly() && metadata.isStandardMetadataFormatSupported()) {
                double dotsPerMilli = 1.0 * 96 / 10 / 2.54;

                IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
                horiz.setAttribute("value", Double.toString(dotsPerMilli));

                IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
                vert.setAttribute("value", Double.toString(dotsPerMilli));

                IIOMetadataNode dim = new IIOMetadataNode("Dimension");
                dim.appendChild(horiz);
                dim.appendChild(vert);

                IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
                root.appendChild(dim);

                metadata.mergeTree("javax_imageio_1.0", root);
            }
            imageWriter.setOutput(ios);
            imageWriter.write(metadata, new IIOImage(bufferedImage, null, metadata), imageWriteParam);
        }
    }
}
