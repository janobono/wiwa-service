package sk.janobono.wiwa.business.impl.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import sk.janobono.wiwa.business.impl.component.image.BaseImageUtil;
import sk.janobono.wiwa.business.impl.model.pdf.*;
import sk.janobono.wiwa.business.model.application.OrderPropertiesData;
import sk.janobono.wiwa.business.model.application.UnitData;
import sk.janobono.wiwa.business.model.order.*;
import sk.janobono.wiwa.business.model.order.part.PartCornerData;
import sk.janobono.wiwa.business.model.order.part.PartCornerRoundedData;
import sk.janobono.wiwa.business.model.order.part.PartCornerStraightData;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.component.ImageUtil;
import sk.janobono.wiwa.config.CommonConfigProperties;
import sk.janobono.wiwa.model.*;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OrderHtmlUtilService {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final CommonConfigProperties commonConfigProperties;

    private final TemplateEngine templateEngine;

    private final ImageUtil imageUtil;

    private final ApplicationPropertyService applicationPropertyService;

    private final MaterialUtilService materialUtilService;

    public String generateHtml(final OrderData order) {
        final OrderPropertiesData orderProperties = applicationPropertyService.getOrderProperties();
        return templateEngine.process(
                "OrderTemplate",
                getContext(orderProperties, orderToPdfContent(orderProperties, order))
        );
    }

    private IContext getContext(final OrderPropertiesData orderProperties, final PdfContentData pdfContent) {
        final Context context = new Context();
        context.setVariable("pdfProperties", orderProperties);
        context.setVariable("pdfContent", pdfContent);
        return context;
    }

    public PdfContentData orderToPdfContent(final OrderPropertiesData orderProperties, final OrderData order) {
        final List<UnitData> units = applicationPropertyService.getUnits();

        final Map<Long, String> materialNames = materialUtilService.getMaterialNames(order.boards(),
                applicationPropertyService.getBoardMaterialCategory(),
                orderProperties.content().get(OrderContent.MATERIAL_NOT_FOUND));

        return PdfContentData.builder()
                .title(formatted(OrderFormat.PDF_TITLE, orderProperties, formatted(OrderFormat.PDF_ORDER_NUMBER, orderProperties, order.orderNumber())))

                .creator(formatToPdf(order.creator()))
                .created(DATE_TIME_FORMAT.format(order.created()))
                .orderNumber(formatted(OrderFormat.PDF_ORDER_NUMBER, orderProperties, order.orderNumber()))
                .deliveryDate(order.deliveryDate() == null ? "" : DATE_FORMAT.format(order.deliveryDate()))
                .packageType(order.packageType() == null
                        ? orderProperties.packageType().getOrDefault(OrderPackageType.NO_PACKAGE, OrderPackageType.NO_PACKAGE.name())
                        : orderProperties.packageType().getOrDefault(order.packageType(), order.packageType().name()))

                .orderContact(Optional.ofNullable(order.contact()).orElseGet(() -> OrderContactData.builder()
                                .name("")
                                .street("")
                                .zipCode("")
                                .city("")
                                .state("")
                                .phone("")
                                .email("")
                                .businessId("")
                                .taxId("")
                                .build()
                        )
                )

                .summary(orderToPdfSummary(orderProperties, units, materialNames, order))

                .items(orderToPdfItems(orderProperties, units, materialNames, order))

                .build();
    }

    private PdfSummaryData orderToPdfSummary(final OrderPropertiesData orderProperties,
                                             final List<UnitData> units,
                                             final Map<Long, String> materialNames,
                                             final OrderData order) {
        return PdfSummaryData.builder()
                .boardSummary(orderToPdfBoardSummary(orderProperties, units, materialNames, order))
                .edgeSummary(orderToPdfEdgeSummary(orderProperties, units, order))
                .glueSummary(orderToPdfGlueSummary(orderProperties, units, order))
                .cutSummary(orderToPdfCutSummary(orderProperties, units, order))
                .weight(unitKilogram(orderProperties, units, order.summary().weight()))
                .total(price(orderProperties, order.summary().total()))
                .vatTotal(price(orderProperties, order.summary().vatTotal()))
                .build();
    }

    private List<PdfBoardSummaryData> orderToPdfBoardSummary(final OrderPropertiesData orderProperties,
                                                             final List<UnitData> units,
                                                             final Map<Long, String> materialNames,
                                                             final OrderData order) {
        return order.summary().boardSummary().stream()
                .map(item -> PdfBoardSummaryData.builder()
                        .material(materialNames.getOrDefault(item.id(), orderProperties.content().get(OrderContent.MATERIAL_NOT_FOUND)))
                        .name(materialUtilService.getDecor(order.boards(), item.id(), orderProperties.content().get(OrderContent.BOARD_NOT_FOUND)))
                        .area(unitSquareMeter(orderProperties, units, item.area()))
                        .boardsCount(unitPiece(orderProperties, units, item.boardsCount()))
                        .weight(unitKilogram(orderProperties, units, item.weight()))
                        .price(price(orderProperties, item.price()))
                        .vatPrice(price(orderProperties, item.vatPrice()))
                        .build())
                .toList();
    }

    private List<PdfEdgeSummaryData> orderToPdfEdgeSummary(final OrderPropertiesData orderProperties,
                                                           final List<UnitData> units,
                                                           final OrderData order) {
        return order.summary().edgeSummary().stream()
                .map(item -> PdfEdgeSummaryData.builder()
                        .name(materialUtilService.getEdge(orderProperties.format().get(OrderFormat.PDF_EDGE), order.edges(), item.id(),
                                orderProperties.content().get(OrderContent.EDGE_NOT_FOUND)))
                        .length(unitMeter(orderProperties, units, item.length()))
                        .glueLength(unitMeter(orderProperties, units, item.glueLength()))
                        .weight(unitKilogram(orderProperties, units, item.weight()))
                        .edgePrice(price(orderProperties, item.edgePrice()))
                        .edgeVatPrice(price(orderProperties, item.edgeVatPrice()))
                        .gluePrice(price(orderProperties, item.gluePrice()))
                        .glueVatPrice(price(orderProperties, item.glueVatPrice()))
                        .build())
                .toList();
    }

    private PdfGlueSummaryData orderToPdfGlueSummary(final OrderPropertiesData orderProperties,
                                                     final List<UnitData> units,
                                                     final OrderData order) {
        return PdfGlueSummaryData.builder()
                .area(unitSquareMeter(orderProperties, units, order.summary().glueSummary().area()))
                .price(price(orderProperties, order.summary().glueSummary().price()))
                .vatPrice(price(orderProperties, order.summary().glueSummary().vatPrice()))
                .build();
    }

    private List<PdfCutSummaryData> orderToPdfCutSummary(final OrderPropertiesData orderProperties,
                                                         final List<UnitData> units,
                                                         final OrderData order) {
        return order.summary().cutSummary().stream()
                .map(item -> PdfCutSummaryData.builder()
                        .thickness(unitMillimeter(orderProperties, units, item.thickness()))
                        .amount(unitMeter(orderProperties, units, item.amount()))
                        .price(price(orderProperties, item.price()))
                        .vatPrice(price(orderProperties, item.vatPrice()))
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

    private String unitPiece(final OrderPropertiesData orderProperties, final List<UnitData> units, final BigDecimal value) {
        return formatted(
                OrderFormat.PDF_INTEGER,
                orderProperties,
                Optional.ofNullable(value).map(BigDecimal::intValue).orElse(0),
                getUnit(Unit.PIECE, units)
        );
    }

    private String unitKilogram(final OrderPropertiesData orderProperties, final List<UnitData> units, final BigDecimal value) {
        return formatted(
                OrderFormat.PDF_UNIT,
                orderProperties,
                Optional.ofNullable(value).orElse(BigDecimal.ZERO),
                getUnit(Unit.KILOGRAM, units)
        );
    }

    private String unitMeter(final OrderPropertiesData orderProperties, final List<UnitData> units, final BigDecimal value) {
        return formatted(
                OrderFormat.PDF_UNIT,
                orderProperties,
                Optional.ofNullable(value).orElse(BigDecimal.ZERO),
                getUnit(Unit.METER, units)
        );
    }

    private String unitMillimeter(final OrderPropertiesData orderProperties, final List<UnitData> units, final BigDecimal value) {
        return formatted(
                OrderFormat.PDF_INTEGER,
                orderProperties,
                Optional.ofNullable(value).map(BigDecimal::intValue).orElse(0),
                getUnit(Unit.MILLIMETER, units)
        );
    }

    private String unitSquareMeter(final OrderPropertiesData orderProperties, final List<UnitData> units, final BigDecimal value) {
        return formatted(
                OrderFormat.PDF_UNIT,
                orderProperties,
                Optional.ofNullable(value).orElse(BigDecimal.ZERO),
                getUnit(Unit.SQUARE_METER, units)
        );
    }

    private String price(final OrderPropertiesData orderProperties, final BigDecimal value) {
        return formatted(
                OrderFormat.PDF_PRICE,
                orderProperties,
                Optional.ofNullable(value).orElse(BigDecimal.ZERO),
                commonConfigProperties.currency().symbol()
        );
    }

    private String formatted(final OrderFormat format, final OrderPropertiesData orderProperties, final Object... args) {
        return orderProperties.format().get(format).formatted(args);
    }

    private List<PdfItemData> orderToPdfItems(final OrderPropertiesData orderProperties,
                                              final List<UnitData> units,
                                              final Map<Long, String> materialNames,
                                              final OrderData order) {
        return order.items().stream()
                .map(item -> {
                    final List<OrderItemImageData> itemImages = BaseImageUtil.partImages(orderProperties, item.part());
                    return PdfItemData.builder()
                            .partNum(Integer.toString(item.sortNum() + 1))
                            .name(item.name())
                            .dimX(unitMillimeter(orderProperties, units, item.part().dimensions().get(BoardPosition.TOP).x()))
                            .dimY(unitMillimeter(orderProperties, units, item.part().dimensions().get(BoardPosition.TOP).y()))
                            .quantity(unitPiece(orderProperties, units, BigDecimal.valueOf(item.quantity())))
                            .description(item.description())
                            .image(getImage(ItemImage.FULL, itemImages))
                            .boards(toBoards(orderProperties, units, materialNames, order, item, itemImages))
                            .edges(toEdges(orderProperties, order, item))
                            .corners(toCorners(orderProperties, units, item))
                            .build();
                })
                .toList();
    }

    private String getImage(final ItemImage itemImage, final List<OrderItemImageData> itemImages) {
        return itemImages.stream().filter(image -> image.itemImage() == itemImage).findFirst()
                .map(image -> imageUtil.toBase64(image.mimeType(), image.image()))
                .orElse(null);
    }

    private List<PdfItemBoardData> toBoards(final OrderPropertiesData orderProperties,
                                            final List<UnitData> units,
                                            final Map<Long, String> materialNames,
                                            final OrderData order,
                                            final OrderItemData item,
                                            final List<OrderItemImageData> itemImages) {
        final List<PdfItemBoardData> result = new LinkedList<>();
        for (final BoardPosition boardPosition : BoardPosition.values()) {
            if (item.part().boards().containsKey(boardPosition)) {
                final var value = item.part().boards().get(boardPosition);
                result.add(PdfItemBoardData.builder()
                        .position(orderProperties.boards().getOrDefault(boardPosition, boardPosition.name()))
                        .material(materialNames.getOrDefault(value, orderProperties.content().get(OrderContent.MATERIAL_NOT_FOUND)))
                        .name(materialUtilService.getDecor(order.boards(), value, orderProperties.content().get(OrderContent.BOARD_NOT_FOUND)))
                        .dimX(unitMillimeter(orderProperties, units, item.part().dimensions().get(boardPosition).x()))
                        .dimY(unitMillimeter(orderProperties, units, item.part().dimensions().get(boardPosition).y()))
                        .image(getImage(ItemImage.valueOf(boardPosition.name()), itemImages))
                        .build());
            }
        }
        return result;
    }

    private List<PdfItemEdgeData> toEdges(final OrderPropertiesData orderProperties,
                                          final OrderData order,
                                          final OrderItemData item) {
        final List<PdfItemEdgeData> result = new LinkedList<>();
        for (final EdgePosition edgePosition : EdgePosition.values()) {
            if (item.part().edges().containsKey(edgePosition)) {
                result.add(new PdfItemEdgeData(
                        orderProperties.edges().getOrDefault(edgePosition, edgePosition.name()),
                        materialUtilService.getEdge(orderProperties.format().get(OrderFormat.PDF_EDGE), order.edges(),
                                item.part().edges().get(edgePosition),
                                orderProperties.content().get(OrderContent.EDGE_NOT_FOUND))
                ));
            }
        }
        return result;
    }

    private List<PdfItemCornerData> toCorners(final OrderPropertiesData orderProperties,
                                              final List<UnitData> units,
                                              final OrderItemData item) {
        final List<PdfItemCornerData> result = new LinkedList<>();
        for (final CornerPosition cornerPosition : CornerPosition.values()) {
            if (item.part().corners().containsKey(cornerPosition)) {
                result.add(new PdfItemCornerData(
                        orderProperties.corners().getOrDefault(cornerPosition, cornerPosition.name()),
                        toCornerName(orderProperties, units, item.part().corners().get(cornerPosition))
                ));
            }
        }
        return result;
    }

    private String toCornerName(final OrderPropertiesData orderProperties, final List<UnitData> units, final PartCornerData partCorner) {
        return switch (partCorner) {
            case final PartCornerStraightData partCornerStraightData ->
                    orderProperties.format().get(OrderFormat.PDF_CORNER_STRAIGHT).formatted(
                            partCornerStraightData.dimensions().x().intValue(),
                            getUnit(Unit.MILLIMETER, units),
                            partCornerStraightData.dimensions().y().intValue(),
                            getUnit(Unit.MILLIMETER, units)
                    );
            case final PartCornerRoundedData partCornerRoundedData ->
                    orderProperties.format().get(OrderFormat.PDF_CORNER_ROUNDED).formatted(
                            partCornerRoundedData.radius().intValue(),
                            getUnit(Unit.MILLIMETER, units)
                    );
            default ->
                    throw new InvalidParameterException("Unsupported part corner type: " + partCorner.getClass().getSimpleName());
        };
    }
}