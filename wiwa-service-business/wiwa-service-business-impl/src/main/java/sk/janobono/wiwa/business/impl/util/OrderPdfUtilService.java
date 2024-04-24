package sk.janobono.wiwa.business.impl.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.dal.domain.OrderViewDo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class OrderPdfUtilService {

    public Path generatePdf(final OrderViewDo orderViewDo) {
        try (final PDDocument doc = new PDDocument()) {

            // TODO

            final Path path = Files.createTempFile("wiwa", ".pdf");
            doc.save(path.toFile());
            return path;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
