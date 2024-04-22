package sk.janobono.wiwa.business.impl.component;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.business.model.order.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class OrderPdfUtil {

    public void init() {
    }

    public void cleanUp() {
    }

    public Path createDirectory() {
        final Path path;
        try {
            path = Files.createTempDirectory("pdf");
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return path;
    }

    public void deleteDirectory(final Path dir) {
        if (dir != null) {
            deleteDirectory(dir.toFile());
        }
    }

    public Path createPdf(
            OrderData orderData,
            List<OrderCommentData> orderComments,
            List<OrderItemData> orderItems,
            OrderSummaryData orderSummary,
            SendOrderData sendOrder) {
        // TODO
        return null;
    }

    private void deleteDirectory(final File dir) {
        final File[] allContents = dir.listFiles();
        if (allContents != null) {
            for (final File file : allContents) {
                deleteDirectory(file);
            }
        }
        dir.delete();
    }
}
