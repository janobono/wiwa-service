package sk.janobono.wiwa.business.impl.util;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;
import sk.janobono.wiwa.business.model.application.PDFPropertiesData;
import sk.janobono.wiwa.business.model.order.OrderData;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
@Service
public class OrderPdfUtilService {

    private final TemplateEngine templateEngine;

    private final ApplicationPropertyService applicationPropertyService;

    public Path generatePdf(final OrderData order) {
        final Path path;
        try {
            path = Files.createTempFile("wiwa", ".pdf");
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        final Document document = Jsoup.parse(format(applicationPropertyService.getPDFProperties(), order));
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

        try (final OutputStream outputStream = new FileOutputStream(path.toFile())) {
            final ITextRenderer renderer = new ITextRenderer();
            final SharedContext sharedContext = renderer.getSharedContext();
            sharedContext.setPrint(true);
            sharedContext.setInteractive(false);
            renderer.setDocumentFromString(document.html());
            renderer.layout();
            renderer.createPDF(outputStream);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return path;
    }

    private String format(final PDFPropertiesData pdfPropertiesData, final OrderData orderData) {
        return templateEngine.process("OrderTemplate", getContext(pdfPropertiesData, orderData));
    }

    private IContext getContext(final PDFPropertiesData pdfPropertiesData, final OrderData orderData) {
        final Context context = new Context();
        context.setVariable("title", pdfPropertiesData.titleFormat().formatted(orderData.orderNumber()));
        // TODO
        return context;
    }
}
