package sk.janobono.wiwa.business.impl.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import sk.janobono.wiwa.business.impl.component.image.BaseImageUtil;
import sk.janobono.wiwa.business.impl.model.html.*;
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
import java.text.MessageFormat;
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
                getContext(orderProperties, orderToHtmlContent(orderProperties, order))
        );
    }

    private IContext getContext(final OrderPropertiesData orderProperties, final HtmlContentData htmlContentData) {
        final Context context = new Context();
        context.setVariable("orderProperties", orderProperties);
        context.setVariable("htmlContent", htmlContentData);
        return context;
    }

    public HtmlContentData orderToHtmlContent(final OrderPropertiesData orderProperties, final OrderData order) {
        final List<UnitData> units = applicationPropertyService.getUnits();

        final Map<Long, String> materialNames = materialUtilService.getMaterialNames(order.boards(),
                applicationPropertyService.getBoardMaterialCategory(),
                orderProperties.content().get(OrderContent.MATERIAL_NOT_FOUND));

        return HtmlContentData.builder()
                .title(formatted(OrderPattern.PDF_TITLE, orderProperties, formatted(OrderPattern.PDF_ORDER_NUMBER, orderProperties, order.orderNumber())))

                .creator(formatToHtml(order.creator()))
                .created(DATE_TIME_FORMAT.format(order.created()))
                .orderNumber(formatted(OrderPattern.PDF_ORDER_NUMBER, orderProperties, order.orderNumber()))
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

                .summary(orderToHtmlSummary(orderProperties, units, materialNames, order))

                .items(orderToPdfItems(orderProperties, units, materialNames, order))

                .build();
    }

    private HtmlSummaryData orderToHtmlSummary(final OrderPropertiesData orderProperties,
                                               final List<UnitData> units,
                                               final Map<Long, String> materialNames,
                                               final OrderData order) {
        return HtmlSummaryData.builder()
                .boardSummary(orderToHtmlBoardSummary(orderProperties, units, materialNames, order))
                .edgeSummary(orderToHtmlEdgeSummary(orderProperties, units, order))
                .glueSummary(orderToHtmlGlueSummary(orderProperties, units, order))
                .cutSummary(orderToHtmlCutSummary(orderProperties, units, order))
                .weight(unitKilogram(orderProperties, units, order.summary().weight()))
                .total(price(orderProperties, order.summary().total()))
                .vatTotal(price(orderProperties, order.summary().vatTotal()))
                .build();
    }

    private List<HtmlBoardSummaryData> orderToHtmlBoardSummary(final OrderPropertiesData orderProperties,
                                                               final List<UnitData> units,
                                                               final Map<Long, String> materialNames,
                                                               final OrderData order) {
        return order.summary().boardSummary().stream()
                .map(item -> HtmlBoardSummaryData.builder()
                        .material(materialNames.getOrDefault(item.id(), orderProperties.content().get(OrderContent.MATERIAL_NOT_FOUND)))
                        .name(materialUtilService.getDecor(orderProperties.pattern().get(OrderPattern.PDF_DECOR), order.boards(), item.id(), orderProperties.content().get(OrderContent.BOARD_NOT_FOUND)))
                        .area(unitSquareMeter(orderProperties, units, item.area()))
                        .boardsCount(unitPiece(orderProperties, units, item.boardsCount()))
                        .weight(unitKilogram(orderProperties, units, item.weight()))
                        .price(price(orderProperties, item.price()))
                        .vatPrice(price(orderProperties, item.vatPrice()))
                        .build())
                .toList();
    }

    private List<HtmlEdgeSummaryData> orderToHtmlEdgeSummary(final OrderPropertiesData orderProperties,
                                                             final List<UnitData> units,
                                                             final OrderData order) {
        return order.summary().edgeSummary().stream()
                .map(item -> HtmlEdgeSummaryData.builder()
                        .name(materialUtilService.getEdge(orderProperties.pattern().get(OrderPattern.PDF_EDGE), order.edges(), item.id(),
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

    private HtmlGlueSummaryData orderToHtmlGlueSummary(final OrderPropertiesData orderProperties,
                                                       final List<UnitData> units,
                                                       final OrderData order) {
        return HtmlGlueSummaryData.builder()
                .area(unitSquareMeter(orderProperties, units, order.summary().glueSummary().area()))
                .price(price(orderProperties, order.summary().glueSummary().price()))
                .vatPrice(price(orderProperties, order.summary().glueSummary().vatPrice()))
                .build();
    }

    private List<HtmlCutSummaryData> orderToHtmlCutSummary(final OrderPropertiesData orderProperties,
                                                           final List<UnitData> units,
                                                           final OrderData order) {
        return order.summary().cutSummary().stream()
                .map(item -> HtmlCutSummaryData.builder()
                        .thickness(unitMillimeter(orderProperties, units, item.thickness()))
                        .amount(unitMeter(orderProperties, units, item.amount()))
                        .price(price(orderProperties, item.price()))
                        .vatPrice(price(orderProperties, item.vatPrice()))
                        .build())
                .toList();

    }

    private String formatToHtml(final OrderUserData creator) {
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
                OrderPattern.PDF_INTEGER,
                orderProperties,
                Optional.ofNullable(value).map(BigDecimal::intValue).orElse(0),
                getUnit(Unit.PIECE, units)
        );
    }

    private String unitKilogram(final OrderPropertiesData orderProperties, final List<UnitData> units, final BigDecimal value) {
        return formatted(
                OrderPattern.PDF_UNIT,
                orderProperties,
                Optional.ofNullable(value).orElse(BigDecimal.ZERO),
                getUnit(Unit.KILOGRAM, units)
        );
    }

    private String unitMeter(final OrderPropertiesData orderProperties, final List<UnitData> units, final BigDecimal value) {
        return formatted(
                OrderPattern.PDF_UNIT,
                orderProperties,
                Optional.ofNullable(value).orElse(BigDecimal.ZERO),
                getUnit(Unit.METER, units)
        );
    }

    private String unitMillimeter(final OrderPropertiesData orderProperties, final List<UnitData> units, final BigDecimal value) {
        return formatted(
                OrderPattern.PDF_INTEGER,
                orderProperties,
                Optional.ofNullable(value).map(BigDecimal::intValue).orElse(0),
                getUnit(Unit.MILLIMETER, units)
        );
    }

    private String unitSquareMeter(final OrderPropertiesData orderProperties, final List<UnitData> units, final BigDecimal value) {
        return formatted(
                OrderPattern.PDF_UNIT,
                orderProperties,
                Optional.ofNullable(value).orElse(BigDecimal.ZERO),
                getUnit(Unit.SQUARE_METER, units)
        );
    }

    private String price(final OrderPropertiesData orderProperties, final BigDecimal value) {
        return formatted(
                OrderPattern.PDF_PRICE,
                orderProperties,
                Optional.ofNullable(value).orElse(BigDecimal.ZERO),
                commonConfigProperties.currency().symbol()
        );
    }

    private List<HtmlItemData> orderToPdfItems(final OrderPropertiesData orderProperties,
                                               final List<UnitData> units,
                                               final Map<Long, String> materialNames,
                                               final OrderData order) {
        return order.items().stream()
                .map(item -> {
                    final List<OrderItemImageData> itemImages = BaseImageUtil.partImages(orderProperties, item.part());
                    return HtmlItemData.builder()
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

    private List<HtmlItemBoardData> toBoards(final OrderPropertiesData orderProperties,
                                             final List<UnitData> units,
                                             final Map<Long, String> materialNames,
                                             final OrderData order,
                                             final OrderItemData item,
                                             final List<OrderItemImageData> itemImages) {
        final List<HtmlItemBoardData> result = new LinkedList<>();
        for (final BoardPosition boardPosition : BoardPosition.values()) {
            if (item.part().boards().containsKey(boardPosition)) {
                final var value = item.part().boards().get(boardPosition);
                result.add(HtmlItemBoardData.builder()
                        .position(orderProperties.boards().getOrDefault(boardPosition, boardPosition.name()))
                        .material(materialNames.getOrDefault(value, orderProperties.content().get(OrderContent.MATERIAL_NOT_FOUND)))
                        .name(materialUtilService.getDecor(orderProperties.pattern().get(OrderPattern.PDF_DECOR), order.boards(), value, orderProperties.content().get(OrderContent.BOARD_NOT_FOUND)))
                        .dimX(unitMillimeter(orderProperties, units, item.part().dimensions().get(boardPosition).x()))
                        .dimY(unitMillimeter(orderProperties, units, item.part().dimensions().get(boardPosition).y()))
                        .image(getImage(ItemImage.valueOf(boardPosition.name()), itemImages))
                        .build());
            }
        }
        return result;
    }

    private List<HtmlItemEdgeData> toEdges(final OrderPropertiesData orderProperties,
                                           final OrderData order,
                                           final OrderItemData item) {
        final List<HtmlItemEdgeData> result = new LinkedList<>();
        for (final EdgePosition edgePosition : EdgePosition.values()) {
            if (item.part().edges().containsKey(edgePosition)) {
                result.add(new HtmlItemEdgeData(
                        orderProperties.edges().getOrDefault(edgePosition, edgePosition.name()),
                        materialUtilService.getEdge(orderProperties.pattern().get(OrderPattern.PDF_EDGE), order.edges(),
                                item.part().edges().get(edgePosition),
                                orderProperties.content().get(OrderContent.EDGE_NOT_FOUND))
                ));
            }
        }
        return result;
    }

    private List<HtmlItemCornerData> toCorners(final OrderPropertiesData orderProperties,
                                               final List<UnitData> units,
                                               final OrderItemData item) {
        final List<HtmlItemCornerData> result = new LinkedList<>();
        for (final CornerPosition cornerPosition : CornerPosition.values()) {
            if (item.part().corners().containsKey(cornerPosition)) {
                result.add(new HtmlItemCornerData(
                        orderProperties.corners().getOrDefault(cornerPosition, cornerPosition.name()),
                        toCornerName(orderProperties, units, item.part().corners().get(cornerPosition))
                ));
            }
        }
        return result;
    }

    private String toCornerName(final OrderPropertiesData orderProperties, final List<UnitData> units, final PartCornerData partCorner) {
        return switch (partCorner) {
            case final PartCornerStraightData partCornerStraightData -> formatted(
                    OrderPattern.PDF_CORNER_STRAIGHT,
                    orderProperties,
                    partCornerStraightData.dimensions().x().intValue(),
                    getUnit(Unit.MILLIMETER, units),
                    partCornerStraightData.dimensions().y().intValue(),
                    getUnit(Unit.MILLIMETER, units)
            );
            case final PartCornerRoundedData partCornerRoundedData -> formatted(
                    OrderPattern.PDF_CORNER_ROUNDED,
                    orderProperties,
                    partCornerRoundedData.radius().intValue(),
                    getUnit(Unit.MILLIMETER, units)
            );
            default ->
                    throw new InvalidParameterException("Unsupported part corner type: " + partCorner.getClass().getSimpleName());
        };
    }

    private String formatted(final OrderPattern pattern, final OrderPropertiesData orderProperties, final Object... args) {
        return MessageFormat.format(orderProperties.pattern().get(pattern), args);
    }
}
