package sk.janobono.wiwa.component;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.MessageFormat;
import java.util.Base64;

@Component
public class ImageUtil {

    public boolean isImageFile(final String fileType) {
        return fileType.equals(MediaType.IMAGE_GIF_VALUE)
                || fileType.equals(MediaType.IMAGE_JPEG_VALUE)
                || fileType.equals(MediaType.IMAGE_PNG_VALUE);
    }

    public byte[] getFileData(final MultipartFile file) {
        try (
                final InputStream is = new BufferedInputStream(file.getInputStream());
                final ByteArrayOutputStream os = new ByteArrayOutputStream()
        ) {
            read(is, os);
            return os.toByteArray();
        } catch (final Exception e) {
            throw new RuntimeException("Local storage exception.", e);
        }
    }

    public Resource getDataResource(final byte[] data) {
        return new ByteArrayResource(data);
    }

    public String toThumbnail(final String fileType, final byte[] data) {
        return MessageFormat.format("data:{0};base64,{1}", fileType, Base64.getEncoder().encodeToString(data));
    }

    public byte[] scaleImage(final String fileType, final byte[] data, final int maxWidth, final int maxHeight) {
        try {
            return toByteArray(scaleImage(toBufferedImage(data), maxWidth, maxHeight), formatName(fileType));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] scaleImageAndCropToSquare(final String fileType, final byte[] data, final int maxSize) {
        try {
            return toByteArray(scaleImageAndCropToSquare(toBufferedImage(data), maxSize), formatName(fileType));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] generateMessageImage(String message) {
        if (message == null || message.isBlank()) {
            message = "NOT FOUND";
        }

        final Font font = new Font("Arial", Font.PLAIN, 18);

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        final int width = fm.stringWidth(message.toUpperCase());

        final int height = fm.getHeight();
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

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", baos);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }

    private String formatName(final String fileType) {
        return fileType.replaceFirst("image/", "");
    }

    private byte[] toByteArray(final BufferedImage bufferedImage, final String formatName) throws IOException {
        try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            switch (formatName.toLowerCase()) {
                case "jpeg", "jpg" -> processJPEGOutput(byteArrayOutputStream, bufferedImage, formatName);
                case "png" -> processPNGOutput(byteArrayOutputStream, bufferedImage, formatName);
                default -> // unsupported image type will be processed without default compression and DPI modification
                        ImageIO.write(bufferedImage, formatName, byteArrayOutputStream);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }

    private BufferedImage toBufferedImage(final byte[] data) {
        try (final InputStream is = new ByteArrayInputStream(data)) {
            return ImageIO.read(is);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedImage scaleImageAndCropToSquare(final BufferedImage image, final int maxSize) {
        final int height = image.getHeight();
        final int width = image.getWidth();
        final int side = Math.min(width, height);

        final int x = (image.getWidth() - side) / 2;
        final int y = (image.getHeight() - side) / 2;

        final BufferedImage cropped = image.getSubimage(x, y, side, side);
        final Image scaledImg = cropped.getScaledInstance(maxSize, maxSize, Image.SCALE_SMOOTH);
        final BufferedImage scaled = new BufferedImage(scaledImg.getWidth(null), scaledImg.getHeight(null), BufferedImage.TYPE_INT_RGB);
        final Graphics graphics = scaled.getGraphics();
        graphics.drawImage(scaledImg, 0, 0, maxSize, maxSize, null);
        graphics.dispose();
        return scaled;
    }

    private BufferedImage scaleImage(final BufferedImage image, final int maxWidth, final int maxHeight) {
        final int imageWidth = image.getWidth();
        final int imageHeight = image.getHeight();
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
        final int destWidth = (int) (imageWidth * scaleRatio);
        final int destHeight = (int) (imageHeight * scaleRatio);

        final Image scaledImg = image.getScaledInstance(destWidth, destHeight, Image.SCALE_SMOOTH);
        final BufferedImage scaled = new BufferedImage(destWidth, destHeight, image.getType());
        final Graphics graphics = scaled.getGraphics();
        graphics.drawImage(scaledImg, 0, 0, destWidth, destHeight, null);
        graphics.dispose();
        return scaled;
    }

    private void processJPEGOutput(final ByteArrayOutputStream byteArrayOutputStream, final BufferedImage bufferedImage, final String formatName) throws IOException {
        final ImageWriter imageWriter = ImageIO.getImageWritersByFormatName(formatName).next();
        final ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionQuality(0.90f);
        final ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
        final IIOMetadata metadata = imageWriter.getDefaultImageMetadata(typeSpecifier, imageWriteParam);

        try (final ImageOutputStream ios = ImageIO.createImageOutputStream(byteArrayOutputStream)) {
            if (!metadata.isReadOnly() && metadata.isStandardMetadataFormatSupported()) {
                final IIOMetadataNode root = new IIOMetadataNode("javax_imageio_jpeg_image_1.0");
                final IIOMetadataNode jpegVariety = new IIOMetadataNode("JPEGvariety");
                final IIOMetadataNode markerSequence = new IIOMetadataNode("markerSequence");
                final IIOMetadataNode app0JFIF = getMetadataNode();

                root.appendChild(jpegVariety);
                root.appendChild(markerSequence);
                jpegVariety.appendChild(app0JFIF);

                metadata.mergeTree("javax_imageio_jpeg_image_1.0", root);
            }
            imageWriter.setOutput(ios);
            imageWriter.write(metadata, new IIOImage(bufferedImage, null, metadata), imageWriteParam);
        }
    }

    private IIOMetadataNode getMetadataNode() {
        final IIOMetadataNode app0JFIF = new IIOMetadataNode("app0JFIF");

        app0JFIF.setAttribute("majorVersion", "1");
        app0JFIF.setAttribute("minorVersion", "2");
        app0JFIF.setAttribute("thumbWidth", "0");
        app0JFIF.setAttribute("thumbHeight", "0");
        app0JFIF.setAttribute("resUnits", "01");
        app0JFIF.setAttribute("Xdensity", String.valueOf(96));
        app0JFIF.setAttribute("Ydensity", String.valueOf(96));
        return app0JFIF;
    }

    private void processPNGOutput(final ByteArrayOutputStream byteArrayOutputStream, final BufferedImage bufferedImage, final String formatName) throws IOException {
        final ImageWriter imageWriter = ImageIO.getImageWritersByFormatName(formatName).next();
        final ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        final ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
        final IIOMetadata metadata = imageWriter.getDefaultImageMetadata(typeSpecifier, imageWriteParam);

        try (final ImageOutputStream ios = ImageIO.createImageOutputStream(byteArrayOutputStream)) {
            if (!metadata.isReadOnly() && metadata.isStandardMetadataFormatSupported()) {
                final double dotsPerMilli = 1.0 * 96 / 10 / 2.54;

                final IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
                horiz.setAttribute("value", Double.toString(dotsPerMilli));

                final IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
                vert.setAttribute("value", Double.toString(dotsPerMilli));

                final IIOMetadataNode dim = new IIOMetadataNode("DimensionId");
                dim.appendChild(horiz);
                dim.appendChild(vert);

                final IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
                root.appendChild(dim);

                metadata.mergeTree("javax_imageio_1.0", root);
            }
            imageWriter.setOutput(ios);
            imageWriter.write(metadata, new IIOImage(bufferedImage, null, metadata), imageWriteParam);
        }
    }

    private void read(final InputStream is, final ByteArrayOutputStream os) throws IOException {
        final byte[] buffer = new byte[1024];
        int bytesRead = 0;
        while (bytesRead != -1) {
            bytesRead = is.read(buffer);
            if (bytesRead > 0) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }
}
