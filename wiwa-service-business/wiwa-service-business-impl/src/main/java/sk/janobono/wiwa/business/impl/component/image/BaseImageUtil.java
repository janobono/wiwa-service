package sk.janobono.wiwa.business.impl.component.image;

import org.springframework.http.MediaType;
import sk.janobono.wiwa.business.model.application.ApplicationImageData;
import sk.janobono.wiwa.business.model.order.OrderItemPartImageData;
import sk.janobono.wiwa.business.model.order.part.PartData;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

abstract class BaseImageUtil<P extends PartData> {

    public abstract List<OrderItemPartImageData> generateImages(final P part);

    protected ApplicationImageData toApplicationImage(final BufferedImage bufferedImage) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return new ApplicationImageData(
                "part.png",
                MediaType.IMAGE_PNG_VALUE,
                null,
                byteArrayOutputStream.toByteArray()
        );
    }

    protected Font getFont(final P part) {
        return new Font(Font.MONOSPACED, Font.PLAIN, 18);
    }
}
