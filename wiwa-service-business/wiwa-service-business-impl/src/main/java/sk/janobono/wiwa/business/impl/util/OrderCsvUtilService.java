package sk.janobono.wiwa.business.impl.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.impl.component.DataUtil;
import sk.janobono.wiwa.business.impl.component.MaterialUtil;
import sk.janobono.wiwa.business.impl.component.part.PartDuplicatedBasicUtil;
import sk.janobono.wiwa.business.impl.component.part.PartDuplicatedFrameUtil;
import sk.janobono.wiwa.business.impl.component.part.PartFrameUtil;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.application.OrderPropertiesData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.business.model.order.part.*;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.dal.domain.OrderItemDo;
import sk.janobono.wiwa.dal.domain.OrderMaterialDo;
import sk.janobono.wiwa.dal.domain.OrderViewDo;
import sk.janobono.wiwa.dal.repository.OrderItemRepository;
import sk.janobono.wiwa.dal.repository.OrderMaterialRepository;
import sk.janobono.wiwa.model.*;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderCsvUtilService {

    private static final String MATERIAL_NOT_FOUND = "Material not found";
    private static final String BOARD_NOT_FOUND = "Board not found";
    private static final String EDGE_NOT_FOUND = "Edge not found";

    private final ScDf scDf;
    private final DataUtil dataUtil;
    private final MaterialUtil materialUtil;

    private final OrderItemRepository orderItemRepository;
    private final OrderMaterialRepository orderMaterialRepository;

    private final ApplicationPropertyService applicationPropertyService;
    private final MaterialUtilService materialUtilService;

    public String generateCsv(final OrderViewDo orderViewDo) {
        final ManufacturePropertiesData manufactureProperties = applicationPropertyService.getManufactureProperties();
        final OrderPropertiesData orderProperties = applicationPropertyService.getOrderProperties();
        final List<OrderMaterialDo> materials = orderMaterialRepository.findAllByOrderId(orderViewDo.id());
        final List<OrderBoardData> boards = materialUtil.toBoards(materials);
        final Map<Long, String> materialNames = materialUtilService.getMaterialNames(boards, applicationPropertyService.getBoardMaterialCategory(), MATERIAL_NOT_FOUND);
        final List<OrderEdgeData> edges = materialUtil.toEdges(materials);
        final List<OrderItemDo> items = orderItemRepository.findAllByOrderId(orderViewDo.id());

        final StringWriter stringWriter = new StringWriter();
        try (final PrintWriter writer = new PrintWriter(new BufferedWriter(stringWriter))) {
            printLine(writer, orderProperties, Arrays.stream(CSVColumn.values())
                    .map(key -> new AbstractMap.SimpleEntry<>(key, addQuotes(orderProperties.csvColumns().getOrDefault(key, key.name()))))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
            );
            for (final OrderItemDo item : items) {
                printItem(writer, manufactureProperties, orderProperties, boards, edges, materialNames, item);
            }
            writer.flush();

        }
        return stringWriter.toString();
    }

    private void printItem(final PrintWriter writer,
                           final ManufacturePropertiesData manufactureProperties,
                           final OrderPropertiesData orderProperties,
                           final List<OrderBoardData> boards,
                           final List<OrderEdgeData> edges,
                           final Map<Long, String> materialNames,
                           final OrderItemDo item) {
        final PartData part = dataUtil.parseValue(item.getPart(), PartData.class);
        switch (part) {
            case final PartBasicData partBasic ->
                    printBasic(writer, orderProperties, boards, edges, materialNames, item, partBasic);
            case final PartDuplicatedBasicData partDuplicatedBasic ->
                    printDuplicatedBasic(writer, manufactureProperties, orderProperties, boards, edges, materialNames, item, partDuplicatedBasic);
            case final PartFrameData partFrame ->
                    printFrame(writer, manufactureProperties, orderProperties, boards, edges, materialNames, item, partFrame);
            case final PartDuplicatedFrameData partDuplicatedFrame ->
                    printPartDuplicatedFrame(writer, manufactureProperties, orderProperties, boards, edges, materialNames, item, partDuplicatedFrame);
            default -> throw new InvalidParameterException("Unsupported part type: " + part.getClass().getSimpleName());
        }
    }

    private void printBasic(final PrintWriter writer,
                            final OrderPropertiesData orderProperties,
                            final List<OrderBoardData> boards,
                            final List<OrderEdgeData> edges,
                            final Map<Long, String> materialNames,
                            final OrderItemDo item,
                            final PartBasicData part) {
        final Map<CSVColumn, String> data = new HashMap<>();
        addPart(orderProperties,
                boards,
                edges,
                materialNames,
                item,
                part,
                orderProperties.pattern().get(OrderPattern.CSV_BASIC),
                BoardPosition.TOP,
                part.dimensionsTOP(),
                part.orientation(),
                Map.of(
                        CSVColumn.EDGE_A1, part.edgeIdA1(),
                        CSVColumn.EDGE_A2, part.edgeIdA2(),
                        CSVColumn.EDGE_B1, part.edgeIdB1(),
                        CSVColumn.EDGE_B2, part.edgeIdB2()
                ),
                true,
                true,
                data
        );
        printLine(writer, orderProperties, data);
    }

    private void printDuplicatedBasic(final PrintWriter writer,
                                      final ManufacturePropertiesData manufactureProperties,
                                      final OrderPropertiesData orderProperties,
                                      final List<OrderBoardData> boards,
                                      final List<OrderEdgeData> edges,
                                      final Map<Long, String> materialNames,
                                      final OrderItemDo item,
                                      final PartDuplicatedBasicData part) {
        final Map<BoardPosition, DimensionsData> boardDimensions = new PartDuplicatedBasicUtil().calculateBoardDimensions(part, manufactureProperties);

        // TOP
        final Map<CSVColumn, String> data = new HashMap<>();
        addPart(orderProperties,
                boards,
                edges,
                materialNames,
                item,
                part,
                orderProperties.pattern().get(OrderPattern.CSV_DUPLICATED_BASIC),
                BoardPosition.TOP,
                boardDimensions.get(BoardPosition.TOP),
                part.orientation(),
                Map.of(
                        CSVColumn.EDGE_A1, part.edgeIdA1(),
                        CSVColumn.EDGE_A2, part.edgeIdA2(),
                        CSVColumn.EDGE_B1, part.edgeIdB1(),
                        CSVColumn.EDGE_B2, part.edgeIdB2()
                ),
                true,
                true,
                data
        );
        printLine(writer, orderProperties, data);
        data.clear();

        // BOTTOM
        addPart(orderProperties,
                boards,
                edges,
                materialNames,
                item,
                part,
                orderProperties.pattern().get(OrderPattern.CSV_DUPLICATED_BASIC),
                BoardPosition.BOTTOM,
                boardDimensions.get(BoardPosition.BOTTOM),
                part.orientation(),
                Map.of(),
                false,
                false,
                data
        );
        printLine(writer, orderProperties, data);
    }

    private void printFrame(final PrintWriter writer,
                            final ManufacturePropertiesData manufactureProperties,
                            final OrderPropertiesData orderProperties,
                            final List<OrderBoardData> boards,
                            final List<OrderEdgeData> edges,
                            final Map<Long, String> materialNames,
                            final OrderItemDo item,
                            final PartFrameData part) {
        final Map<BoardPosition, DimensionsData> boardDimensions = new PartFrameUtil().calculateBoardDimensions(part, manufactureProperties);
        rotateFrame(part.frameType(), boardDimensions);

        boardDimensions.keySet().stream().sorted().forEach(key -> {
            final Map<CSVColumn, String> data = new HashMap<>();

            addPart(orderProperties,
                    boards,
                    edges,
                    materialNames,
                    item,
                    part,
                    orderProperties.pattern().get(OrderPattern.CSV_FRAME),
                    key,
                    boardDimensions.get(key),
                    false,
                    frameBoardEdges(part, key),
                    false,
                    key == BoardPosition.A1,
                    data
            );

            printLine(writer, orderProperties, data);
        });
    }

    private void printPartDuplicatedFrame(final PrintWriter writer,
                                          final ManufacturePropertiesData manufactureProperties,
                                          final OrderPropertiesData orderProperties,
                                          final List<OrderBoardData> boards,
                                          final List<OrderEdgeData> edges,
                                          final Map<Long, String> materialNames,
                                          final OrderItemDo item,
                                          final PartDuplicatedFrameData part) {
        final Map<BoardPosition, DimensionsData> boardDimensions = new PartDuplicatedFrameUtil().calculateBoardDimensions(part, manufactureProperties);
        rotateFrame(part.frameType(), boardDimensions);

        // TOP
        final Map<CSVColumn, String> data = new HashMap<>();
        addPart(orderProperties,
                boards,
                edges,
                materialNames,
                item,
                part,
                orderProperties.pattern().get(OrderPattern.CSV_DUPLICATED_FRAME),
                BoardPosition.TOP,
                boardDimensions.get(BoardPosition.TOP),
                part.orientation(),
                Map.of(
                        CSVColumn.EDGE_A1, part.edgeIdA1(),
                        CSVColumn.EDGE_A2, part.edgeIdA2(),
                        CSVColumn.EDGE_B1, part.edgeIdB1(),
                        CSVColumn.EDGE_B2, part.edgeIdB2()
                ),
                true,
                true,
                data
        );
        printLine(writer, orderProperties, data);

        // BOTTOM
        boardDimensions.keySet().stream().filter(key -> key != BoardPosition.TOP).sorted().forEach(key -> {
            data.clear();

            final Map<CSVColumn, Long> frameBoardEdges = switch (key) {
                case A1 -> Map.of(
                        CSVColumn.EDGE_A2, part.edgeIdA1I()
                );
                case A2 -> Map.of(
                        CSVColumn.EDGE_A1, part.edgeIdA2I()
                );
                case B1 -> Map.of(
                        CSVColumn.EDGE_B2, part.edgeIdB1I()
                );
                case B2 -> Map.of(
                        CSVColumn.EDGE_B1, part.edgeIdB2I()
                );
                default -> throw new InvalidParameterException("Unsupported board position: " + key);
            };

            addPart(orderProperties,
                    boards,
                    edges,
                    materialNames,
                    item,
                    part,
                    orderProperties.pattern().get(OrderPattern.CSV_DUPLICATED_FRAME),
                    key,
                    boardDimensions.get(key),
                    false,
                    frameBoardEdges,
                    false,
                    false,
                    data
            );

            printLine(writer, orderProperties, data);
        });
    }

    private void rotateFrame(final FrameType frameType, final Map<BoardPosition, DimensionsData> boardDimensions) {
        switch (frameType) {
            case HORIZONTAL_LONG -> {
                boardDimensions.put(BoardPosition.B1, boardDimensions.get(BoardPosition.B1).rotate());
                boardDimensions.put(BoardPosition.B2, boardDimensions.get(BoardPosition.B2).rotate());
            }
            case HORIZONTAL_SHORT -> {
                boardDimensions.put(BoardPosition.A1, boardDimensions.get(BoardPosition.A1).rotate());
                boardDimensions.put(BoardPosition.A2, boardDimensions.get(BoardPosition.A2).rotate());
            }
        }
    }

    private Map<CSVColumn, Long> frameBoardEdges(final PartFrameData part, final BoardPosition position) {
        if (part.frameType() == FrameType.VERTICAL) {
            return switch (position) {
                case A1 -> Map.of(
                        CSVColumn.EDGE_A1, part.edgeIdA1(),
                        CSVColumn.EDGE_A2, part.edgeIdA1I()
                );
                case A2 -> Map.of(
                        CSVColumn.EDGE_A1, part.edgeIdA2I(),
                        CSVColumn.EDGE_A2, part.edgeIdA2()
                );
                case B1 -> Map.of(
                        CSVColumn.EDGE_A1, part.edgeIdA1(),
                        CSVColumn.EDGE_A2, part.edgeIdA2(),
                        CSVColumn.EDGE_B1, part.edgeIdB1(),
                        CSVColumn.EDGE_B2, part.edgeIdB1I()
                );
                case B2 -> Map.of(
                        CSVColumn.EDGE_A1, part.edgeIdA1(),
                        CSVColumn.EDGE_A2, part.edgeIdA2(),
                        CSVColumn.EDGE_B1, part.edgeIdB2I(),
                        CSVColumn.EDGE_B2, part.edgeIdB2()
                );
                default -> throw new InvalidParameterException("Unsupported board position: " + position);
            };
        } else {
            return switch (position) {
                case A1 -> Map.of(
                        CSVColumn.EDGE_A1, part.edgeIdA1(),
                        CSVColumn.EDGE_A2, part.edgeIdA1I(),
                        CSVColumn.EDGE_B1, part.edgeIdB1(),
                        CSVColumn.EDGE_B2, part.edgeIdB2()
                );
                case A2 -> Map.of(
                        CSVColumn.EDGE_A1, part.edgeIdA2I(),
                        CSVColumn.EDGE_A2, part.edgeIdA2(),
                        CSVColumn.EDGE_B1, part.edgeIdB1(),
                        CSVColumn.EDGE_B2, part.edgeIdB2()
                );
                case B1 -> Map.of(
                        CSVColumn.EDGE_B1, part.edgeIdB1(),
                        CSVColumn.EDGE_B2, part.edgeIdB1I()
                );
                case B2 -> Map.of(
                        CSVColumn.EDGE_B1, part.edgeIdB2I(),
                        CSVColumn.EDGE_B2, part.edgeIdB2()
                );
                default -> throw new InvalidParameterException("Unsupported board position: " + position);
            };
        }
    }

    private void addPart(final OrderPropertiesData orderProperties,
                         final List<OrderBoardData> boards,
                         final List<OrderEdgeData> edges,
                         final Map<Long, String> materialNames,
                         final OrderItemDo item,
                         final PartData part,
                         final String namePattern,
                         final BoardPosition position,
                         final DimensionsData dimensions,
                         final boolean orientation,
                         final Map<CSVColumn, Long> partEdges,
                         final boolean corners,
                         final boolean description,
                         final Map<CSVColumn, String> data
    ) {
        // NUMBER
        addNumber(orderProperties, item, position, data);
        // NAME
        addName(orderProperties, namePattern, item, position, part.dimensions().get(position), data);
        // MATERIAL
        data.put(CSVColumn.MATERIAL, getMaterial(materialNames, part.boards().get(position)));
        // DECOR
        data.put(CSVColumn.DECOR, getDecor(orderProperties.pattern().get(OrderPattern.CSV_DECOR), boards, part.boards().get(position)));
        // DIMENSIONS
        addDimensions(dimensions, data);
        // QUANTITY
        data.put(CSVColumn.QUANTITY, toString(item.getQuantity()));
        // ORIENTATION
        data.put(CSVColumn.ORIENTATION, toString(orientation));
        // THICKNESS
        data.put(CSVColumn.THICKNESS, getThickness(boards, part.boards().get(position)));
        // EDGES
        addEdges(orderProperties, edges, partEdges, data);
        // CORNERS
        if (corners) {
            addCorners(orderProperties, edges, part, data);
        }
        // DESCRIPTION
        if (description) {
            data.put(CSVColumn.DESCRIPTION, addQuotes(item.getDescription()));
        }
    }

    private void addNumber(final OrderPropertiesData orderProperties,
                           final OrderItemDo item,
                           final BoardPosition position,
                           final Map<CSVColumn, String> data) {
        data.put(CSVColumn.NUMBER,
                getNumber(orderProperties.pattern().get(OrderPattern.CSV_NUMBER), item.getSortNum() + 1, getPositionName(orderProperties, position)));
    }

    private void addName(final OrderPropertiesData orderProperties,
                         final String pattern,
                         final OrderItemDo item,
                         final BoardPosition position,
                         final DimensionsData dimensions,
                         final Map<CSVColumn, String> data
    ) {
        data.put(CSVColumn.NAME, getName(
                pattern,
                item.getName(),
                getPositionName(orderProperties, position),
                dimensions,
                item.getQuantity()
        ));
    }

    private void addDimensions(final DimensionsData dimensions, final Map<CSVColumn, String> data) {
        // X_DIMENSION
        data.put(CSVColumn.X_DIMENSION, toString(dimensions.x().intValue()));
        // Y_DIMENSION
        data.put(CSVColumn.Y_DIMENSION, toString(dimensions.y().intValue()));
    }

    private void addEdges(final OrderPropertiesData orderProperties,
                          final List<OrderEdgeData> edges,
                          final Map<CSVColumn, Long> partEdges,
                          final Map<CSVColumn, String> data) {
        partEdges.forEach((key, value) -> data.put(key, getEdge(orderProperties.pattern().get(OrderPattern.CSV_EDGE), edges, value)));
    }

    private void addCorners(final OrderPropertiesData orderProperties, final List<OrderEdgeData> edges, final PartData part, final Map<CSVColumn, String> data) {
        part.corners().forEach((key, value) -> {
            data.put(switch (key) {
                        case A1B1 -> CSVColumn.CORNER_A1B1;
                        case A1B2 -> CSVColumn.CORNER_A1B2;
                        case A2B1 -> CSVColumn.CORNER_A2B1;
                        case A2B2 -> CSVColumn.CORNER_A2B2;
                    },
                    getCorner(orderProperties, edges, key, value)
            );
        });
    }

    private String getNumber(final String pattern, final int num, final String positionName) {
        return addQuotes(MessageFormat.format(pattern, num, positionName));
    }

    private String getName(final String pattern,
                           final String itemName,
                           final String positionName,
                           final DimensionsData partDimensions,
                           final Integer quantity) {
        return addQuotes(MessageFormat.format(pattern, itemName, positionName, partDimensions.x().intValue(), partDimensions.y().intValue(), quantity));
    }

    private String getMaterial(final Map<Long, String> materialNames, final long boardId) {
        return addQuotes(materialNames.getOrDefault(boardId, MATERIAL_NOT_FOUND));
    }

    private String getDecor(final String pattern, final List<OrderBoardData> boards, final long boardId) {
        return addQuotes(materialUtilService.getDecor(pattern, boards, boardId, BOARD_NOT_FOUND));
    }

    private String getThickness(final List<OrderBoardData> boards, final long boardId) {
        final Optional<OrderBoardData> board = materialUtilService.findBoard(boards, boardId);
        return board.map(OrderBoardData::thickness).map(BigDecimal::intValue).map(this::toString).orElse("");
    }

    private String getEdge(final String pattern, final List<OrderEdgeData> edges, final Long edgeId) {
        return addQuotes(materialUtilService.getEdge(pattern, edges, edgeId, EDGE_NOT_FOUND));
    }

    private String getCorner(final OrderPropertiesData orderProperties,
                             final List<OrderEdgeData> edges,
                             final CornerPosition position,
                             final PartCornerData partCorner) {
        if (partCorner == null) {
            return addQuotes("");
        }

        final String cornerString = cornerString(orderProperties, position, partCorner);
        final String edgeString = materialUtilService.getEdge(orderProperties.pattern().get(OrderPattern.CSV_EDGE), edges, partCorner.edgeId(), EDGE_NOT_FOUND);

        if (edgeString.isBlank()) {
            return addQuotes(cornerString);
        }
        return addQuotes(cornerString + " " + edgeString);
    }

    private String cornerString(final OrderPropertiesData orderProperties, final CornerPosition position, final PartCornerData partCorner) {
        return switch (partCorner) {
            case final PartCornerStraightData partCornerStraightData -> MessageFormat.format(
                    orderProperties.pattern().get(OrderPattern.CSV_CORNER_STRAIGHT),
                    orderProperties.corners().getOrDefault(position, position.name()),
                    partCornerStraightData.dimensions().x().intValue(),
                    partCornerStraightData.dimensions().y().intValue()
            );
            case final PartCornerRoundedData partCornerRoundedData -> MessageFormat.format(
                    orderProperties.pattern().get(OrderPattern.CSV_CORNER_ROUNDED),
                    orderProperties.corners().getOrDefault(position, position.name()),
                    partCornerRoundedData.radius().intValue()
            );
            default ->
                    throw new InvalidParameterException("Unsupported part corner type: " + partCorner.getClass().getSimpleName());
        };
    }

    private String getPositionName(final OrderPropertiesData orderProperties, final BoardPosition position) {
        return orderProperties.boards().getOrDefault(position, position.name());
    }

    private String addQuotes(final String s) {
        return Optional.ofNullable(s).map(d -> "\"" + d + "\"").orElse("\"\"");
    }

    private String toString(final Boolean b) {
        return Optional.ofNullable(b).map(v -> v ? "1" : "0").orElse("0");
    }

    private String toString(final Integer i) {
        return Optional.ofNullable(i).map(Object::toString).orElse("0");
    }

    private void printLine(final PrintWriter writer, final OrderPropertiesData orderProperties, final Map<CSVColumn, String> data) {
        String s = Arrays.stream(CSVColumn.values())
                .map(key -> data.getOrDefault(key, ""))
                .map(scDf::toDf)
                .map(r -> r == null ? "" : r)
                .reduce("", (s1, s2) -> s1 + orderProperties.csvSeparator() + s2)
                .substring(1);

        for (final String regex : orderProperties.csvReplacements().keySet()) {
            s = s.replaceAll(regex, orderProperties.csvReplacements().get(regex));
        }

        writer.println(s);
    }
}
