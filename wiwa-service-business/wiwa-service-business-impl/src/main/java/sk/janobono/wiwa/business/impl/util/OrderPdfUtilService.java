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
import sk.janobono.wiwa.business.impl.model.pdf.*;
import sk.janobono.wiwa.business.model.application.PDFPropertiesData;
import sk.janobono.wiwa.business.model.application.UnitData;
import sk.janobono.wiwa.business.model.order.OrderData;
import sk.janobono.wiwa.business.model.order.OrderUserData;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.model.OrderPackageType;
import sk.janobono.wiwa.model.PDFFormat;
import sk.janobono.wiwa.model.PdfContent;
import sk.janobono.wiwa.model.Unit;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OrderPdfUtilService {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final CommonConfigProperties commonConfigProperties;

    private final TemplateEngine templateEngine;

    private final ApplicationPropertyService applicationPropertyService;

    private final MaterialUtilService materialUtilService;

    public Path generatePdf(final OrderData order) {
        final Path path;
        try {
            path = Files.createTempFile("wiwa", ".pdf");
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        final PDFPropertiesData pdfProperties = applicationPropertyService.getPDFProperties();

        final Document document = Jsoup.parse(
                templateEngine.process(
                        "OrderTemplate",
                        getContext(pdfProperties, orderToPdfContent(pdfProperties, order))
                ));

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

    private IContext getContext(final PDFPropertiesData pdfProperties, final PdfContentData pdfContent) {
        final Context context = new Context();
        context.setVariable("pdfProperties", pdfProperties);
        context.setVariable("pdfContent", pdfContent);
        return context;
    }

    public PdfContentData orderToPdfContent(final PDFPropertiesData pdfProperties, final OrderData order) {
        final List<UnitData> units = applicationPropertyService.getUnits();

        final Map<Long, String> materialNames = materialUtilService.getMaterialNames(order.boards(),
                applicationPropertyService.getBoardMaterialCategory(),
                pdfProperties.content().get(PdfContent.MATERIAL_NOT_FOUND));

        return PdfContentData.builder()
                .title(formatted(PDFFormat.TITLE, pdfProperties, formatted(PDFFormat.ORDER_NUMBER, pdfProperties, order.orderNumber())))
                .creator(formatToPdf(order.creator()))
                .created(DATE_TIME_FORMAT.format(order.created()))
                .orderNumber(formatted(PDFFormat.ORDER_NUMBER, pdfProperties, order.orderNumber()))
                .deliveryDate(order.deliveryDate() == null ? "" : DATE_FORMAT.format(order.deliveryDate()))
                .packageType(order.packageType() == null
                        ? pdfProperties.packageType().getOrDefault(OrderPackageType.NO_PACKAGE, OrderPackageType.NO_PACKAGE.name())
                        : pdfProperties.packageType().getOrDefault(order.packageType(), order.packageType().name()))

                .orderContact(order.contact())

                .summary(orderToPdfSummary(pdfProperties, units, materialNames, order))

                .build();
    }

    private PdfSummaryData orderToPdfSummary(final PDFPropertiesData pdfProperties,
                                             final List<UnitData> units,
                                             final Map<Long, String> materialNames,
                                             final OrderData order) {
        return PdfSummaryData.builder()
                .boardSummary(orderToPdfBoardSummary(pdfProperties, units, materialNames, order))
                .edgeSummary(orderToPdfEdgeSummary(pdfProperties, units, order))
                .glueSummary(orderToPdfGlueSummary(pdfProperties, units, order))
                .cutSummary(orderToPdfCutSummary(pdfProperties, units, order))
                .weight(unitKilogram(pdfProperties, units, order.summary().weight()))
                .total(price(pdfProperties, order.summary().total()))
                .vatTotal(price(pdfProperties, order.summary().vatTotal()))
                .build();
    }

    private List<PdfBoardSummaryData> orderToPdfBoardSummary(final PDFPropertiesData pdfProperties,
                                                             final List<UnitData> units,
                                                             final Map<Long, String> materialNames,
                                                             final OrderData order) {
        return order.summary().boardSummary().stream()
                .map(item -> PdfBoardSummaryData.builder()
                        .material(materialNames.getOrDefault(item.id(), pdfProperties.content().get(PdfContent.MATERIAL_NOT_FOUND)))
                        .name(materialUtilService.getDecor(order.boards(), item.id(), pdfProperties.content().get(PdfContent.BOARD_NOT_FOUND)))
                        .area(unitSquareMeter(pdfProperties, units, item.area()))
                        .boardsCount(intP(pdfProperties, units, item.boardsCount()))
                        .weight(unitKilogram(pdfProperties, units, item.weight()))
                        .price(price(pdfProperties, item.price()))
                        .vatPrice(price(pdfProperties, item.vatPrice()))
                        .build())
                .toList();
    }

    private List<PdfEdgeSummaryData> orderToPdfEdgeSummary(final PDFPropertiesData pdfProperties,
                                                           final List<UnitData> units,
                                                           final OrderData order) {
        return order.summary().edgeSummary().stream()
                .map(item -> PdfEdgeSummaryData.builder()
                        .name(materialUtilService.getEdge(pdfProperties.format().get(PDFFormat.EDGE_FORMAT), order.edges(), item.id(),
                                pdfProperties.content().get(PdfContent.EDGE_NOT_FOUND)))
                        .length(unitMeter(pdfProperties, units, item.length()))
                        .glueLength(unitSquareMeter(pdfProperties, units, item.glueLength()))
                        .weight(unitKilogram(pdfProperties, units, item.weight()))
                        .edgePrice(price(pdfProperties, item.edgePrice()))
                        .edgeVatPrice(price(pdfProperties, item.edgeVatPrice()))
                        .gluePrice(price(pdfProperties, item.gluePrice()))
                        .glueVatPrice(price(pdfProperties, item.glueVatPrice()))
                        .build())
                .toList();
    }

    private PdfGlueSummaryData orderToPdfGlueSummary(final PDFPropertiesData pdfProperties,
                                                     final List<UnitData> units,
                                                     final OrderData order) {
        return PdfGlueSummaryData.builder()
                .area(unitSquareMeter(pdfProperties, units, order.summary().glueSummary().area()))
                .price(price(pdfProperties, order.summary().glueSummary().price()))
                .vatPrice(price(pdfProperties, order.summary().glueSummary().vatPrice()))
                .build();
    }

    private List<PdfCutSummaryData> orderToPdfCutSummary(final PDFPropertiesData pdfProperties,
                                                         final List<UnitData> units,
                                                         final OrderData order) {
        return order.summary().cutSummary().stream()
                .map(item -> PdfCutSummaryData.builder()
                        .thickness(unitMillimeter(pdfProperties, units, item.thickness()))
                        .amount(unitMeter(pdfProperties, units, item.amount()))
                        .price(price(pdfProperties, item.price()))
                        .vatPrice(price(pdfProperties, item.vatPrice()))
                        .build())
                .toList();

    }

    private String formatToPdf(final OrderUserData creator) {
        if (creator == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();

        if (creator.titleBefore() != null) {
            sb.append(creator.titleBefore()).append(" ");
        }

        sb.append(creator.firstName());

        if (creator.midName() != null) {
            sb.append(" ").append(creator.midName());
        }

        sb.append(" ").append(creator.lastName());

        if (creator.titleAfter() != null) {
            sb.append(" ").append(creator.titleAfter());
        }
        return sb.toString();
    }

    private String getUnit(final Unit unit, final List<UnitData> units) {
        return units.stream().filter(u -> u.id() == unit).findFirst().map(UnitData::value).orElse(unit.name());
    }

    private String intP(final PDFPropertiesData pdfProperties, final List<UnitData> units, final BigDecimal value) {
        return formatted(
                PDFFormat.INTEGER,
                pdfProperties,
                Optional.ofNullable(value).orElse(BigDecimal.ZERO),
                getUnit(Unit.PIECE, units)
        );
    }

    private String unitKilogram(final PDFPropertiesData pdfProperties, final List<UnitData> units, final BigDecimal value) {
        return formatted(
                PDFFormat.UNIT,
                pdfProperties,
                Optional.ofNullable(value).orElse(BigDecimal.ZERO),
                getUnit(Unit.KILOGRAM, units)
        );
    }

    private String unitMeter(final PDFPropertiesData pdfProperties, final List<UnitData> units, final BigDecimal value) {
        return formatted(
                PDFFormat.UNIT,
                pdfProperties,
                Optional.ofNullable(value).orElse(BigDecimal.ZERO),
                getUnit(Unit.METER, units)
        );
    }

    private String unitMillimeter(final PDFPropertiesData pdfProperties, final List<UnitData> units, final BigDecimal value) {
        return formatted(
                PDFFormat.UNIT,
                pdfProperties,
                Optional.ofNullable(value).orElse(BigDecimal.ZERO),
                getUnit(Unit.MILLIMETER, units)
        );
    }

    private String unitSquareMeter(final PDFPropertiesData pdfProperties, final List<UnitData> units, final BigDecimal value) {
        return formatted(
                PDFFormat.UNIT,
                pdfProperties,
                Optional.ofNullable(value).orElse(BigDecimal.ZERO),
                getUnit(Unit.SQUARE_METER, units)
        );
    }

    private String price(final PDFPropertiesData pdfProperties, final BigDecimal value) {
        return formatted(
                PDFFormat.PRICE,
                pdfProperties,
                Optional.ofNullable(value).orElse(BigDecimal.ZERO),
                commonConfigProperties.currency().symbol()
        );
    }

    private String formatted(final PDFFormat format, final PDFPropertiesData pdfProperties, final Object... args) {
        return pdfProperties.format().get(format).formatted(args);
    }
}
