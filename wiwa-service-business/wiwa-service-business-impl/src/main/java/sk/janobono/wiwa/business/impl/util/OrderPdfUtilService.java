package sk.janobono.wiwa.business.impl.util;

import com.ironsoftware.ironpdf.*;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.dal.domain.OrderViewDo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class OrderPdfUtilService {

    public Path generatePdf(final OrderViewDo orderViewDo) {
        try (final PdfDocument pdfDocument = PdfDocument.renderHtmlAsPdf("<h1> ~Hello World~ </h1> Made with IronPDF!")){





            // TODO

            final Path path = Files.createTempFile("wiwa", ".pdf");
            pdfDocument.saveAs(path);
            return path;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
