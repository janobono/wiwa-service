package sk.janobono.wiwa.business.impl.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.impl.component.DataUtil;
import sk.janobono.wiwa.business.impl.component.MaterialUtil;
import sk.janobono.wiwa.business.impl.component.part.PartDuplicatedBasicUtil;
import sk.janobono.wiwa.business.impl.component.part.PartDuplicatedFrameUtil;
import sk.janobono.wiwa.business.impl.component.part.PartFrameUtil;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.CSVPropertiesData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.board.BoardCategoryData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.business.model.order.part.*;
import sk.janobono.wiwa.business.service.ApplicationPropertyService;
import sk.janobono.wiwa.component.ScDf;
import sk.janobono.wiwa.dal.domain.CodeListItemDo;
import sk.janobono.wiwa.dal.domain.OrderItemDo;
import sk.janobono.wiwa.dal.domain.OrderMaterialDo;
import sk.janobono.wiwa.dal.domain.OrderViewDo;
import sk.janobono.wiwa.dal.repository.BoardCodeListItemRepository;
import sk.janobono.wiwa.dal.repository.OrderItemRepository;
import sk.janobono.wiwa.dal.repository.OrderMaterialRepository;
import sk.janobono.wiwa.model.BoardPosition;
import sk.janobono.wiwa.model.CSVColumn;
import sk.janobono.wiwa.model.CornerPosition;
import sk.janobono.wiwa.model.FrameType;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;
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

    private final BoardCodeListItemRepository boardCodeListItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMaterialRepository orderMaterialRepository;

    private final ApplicationPropertyService applicationPropertyService;

    public Path generateCsv(final OrderViewDo orderViewDo) {
        final ManufacturePropertiesData manufactureProperties = applicationPropertyService.getManufactureProperties();
        final CSVPropertiesData csvProperties = applicationPropertyService.getCSVProperties();
        final List<OrderMaterialDo> materials = orderMaterialRepository.findAllByOrderId(orderViewDo.id());
        final List<OrderBoardData> boards = materialUtil.toBoards(materials);
        final Map<Long, String> materialNames = getMaterialNames(boards, applicationPropertyService.getBoardMaterialCategory());
        final List<OrderEdgeData> edges = materialUtil.toEdges(materials);
        final List<OrderItemDo> items = orderItemRepository.findAllByOrderId(orderViewDo.id());

        try {
            final Path path = Files.createTempFile("wiwa", ".csv");
            try (final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(path.toFile(), true)))) {
                printLine(writer, csvProperties, Arrays.stream(CSVColumn.values())
                        .map(key -> new AbstractMap.SimpleEntry<>(key, csvProperties.columns().getOrDefault(key, key.name())))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                );
                for (final OrderItemDo item : items) {
                    printItem(writer, manufactureProperties, csvProperties, boards, edges, materialNames, item);
                }
                writer.flush();
            }
            return path;
        } catch (final IOException e) {
            throw new RuntimeException("Line write error.", e);
        }
    }

    private Map<Long, String> getMaterialNames(final List<OrderBoardData> boards, final BoardCategoryData boardMaterialCategory) {
        final Map<Long, String> result = new HashMap<>();

        for (final OrderBoardData board : boards) {
            final String value = boardCodeListItemRepository.findByBoardId(board.id()).stream()
                    .filter(cat -> Objects.equals(cat.getCodeListId(), boardMaterialCategory.id()))
                    .findFirst()
                    .map(CodeListItemDo::getValue)
                    .orElse(MATERIAL_NOT_FOUND);
            result.put(board.id(), value);
        }

        return result;
    }

    private void printItem(final PrintWriter writer,
                           final ManufacturePropertiesData manufactureProperties,
                           final CSVPropertiesData csvProperties,
                           final List<OrderBoardData> boards,
                           final List<OrderEdgeData> edges,
                           final Map<Long, String> materialNames,
                           final OrderItemDo item) {
        final PartData part = dataUtil.parseValue(item.getPart(), PartData.class);
        switch (part) {
            case final PartBasicData partBasic ->
                    printBasic(writer, csvProperties, boards, edges, materialNames, item, partBasic);
            case final PartDuplicatedBasicData partDuplicatedBasic ->
                    printDuplicatedBasic(writer, manufactureProperties, csvProperties, boards, edges, materialNames, item, partDuplicatedBasic);
            case final PartFrameData partFrame ->
                    printFrame(writer, manufactureProperties, csvProperties, boards, edges, materialNames, item, partFrame);
            case final PartDuplicatedFrameData partDuplicatedFrame ->
                    printPartDuplicatedFrame(writer, manufactureProperties, csvProperties, boards, edges, materialNames, item, partDuplicatedFrame);
            default -> throw new InvalidParameterException("Unsupported part type: " + part.getClass().getSimpleName());
        }
    }

    private void printBasic(final PrintWriter writer,
                            final CSVPropertiesData csvProperties,
                            final List<OrderBoardData> boards,
                            final List<OrderEdgeData> edges,
                            final Map<Long, String> materialNames,
                            final OrderItemDo item,
                            final PartBasicData part) {
        final Map<CSVColumn, String> data = new HashMap<>();
        addPart(csvProperties,
                boards,
                edges,
                materialNames,
                item,
                part,
                csvProperties.nameBasicFormat(),
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
                data
        );
        printLine(writer, csvProperties, data);
    }

    private void printDuplicatedBasic(final PrintWriter writer,
                                      final ManufacturePropertiesData manufactureProperties,
                                      final CSVPropertiesData csvProperties,
                                      final List<OrderBoardData> boards,
                                      final List<OrderEdgeData> edges,
                                      final Map<Long, String> materialNames,
                                      final OrderItemDo item,
                                      final PartDuplicatedBasicData part) {
        final Map<BoardPosition, DimensionsData> boardDimensions = new PartDuplicatedBasicUtil().calculateBoardDimensions(part, manufactureProperties);

        // TOP
        final Map<CSVColumn, String> data = new HashMap<>();
        addPart(csvProperties,
                boards,
                edges,
                materialNames,
                item,
                part,
                csvProperties.nameDuplicatedBasicFormat(),
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
                data
        );
        printLine(writer, csvProperties, data);
        data.clear();

        // BOTTOM
        addPart(csvProperties,
                boards,
                edges,
                materialNames,
                item,
                part,
                csvProperties.nameDuplicatedBasicFormat(),
                BoardPosition.BOTTOM,
                boardDimensions.get(BoardPosition.BOTTOM),
                part.orientation(),
                Map.of(),
                false,
                data
        );
        printLine(writer, csvProperties, data);
    }

    private void printFrame(final PrintWriter writer,
                            final ManufacturePropertiesData manufactureProperties,
                            final CSVPropertiesData csvProperties,
                            final List<OrderBoardData> boards,
                            final List<OrderEdgeData> edges,
                            final Map<Long, String> materialNames,
                            final OrderItemDo item,
                            final PartFrameData part) {
        final Map<BoardPosition, DimensionsData> boardDimensions = new PartFrameUtil().calculateBoardDimensions(part, manufactureProperties);
        rotateFrame(part.frameType(), boardDimensions);

        boardDimensions.forEach((key, value) -> {
            final Map<CSVColumn, String> data = new HashMap<>();

            addPart(csvProperties,
                    boards,
                    edges,
                    materialNames,
                    item,
                    part,
                    csvProperties.nameFrameFormat(),
                    key,
                    boardDimensions.get(key),
                    false,
                    frameBoardEdges(part, key),
                    key == BoardPosition.A1,
                    data
            );

            printLine(writer, csvProperties, data);
        });
    }

    private void printPartDuplicatedFrame(final PrintWriter writer,
                                          final ManufacturePropertiesData manufactureProperties,
                                          final CSVPropertiesData csvProperties,
                                          final List<OrderBoardData> boards,
                                          final List<OrderEdgeData> edges,
                                          final Map<Long, String> materialNames,
                                          final OrderItemDo item,
                                          final PartDuplicatedFrameData part) {
        final Map<BoardPosition, DimensionsData> boardDimensions = new PartDuplicatedFrameUtil().calculateBoardDimensions(part, manufactureProperties);
        rotateFrame(part.frameType(), boardDimensions);

        // TOP
        final Map<CSVColumn, String> data = new HashMap<>();
        addPart(csvProperties,
                boards,
                edges,
                materialNames,
                item,
                part,
                csvProperties.nameDuplicatedFrameFormat(),
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
                data
        );
        printLine(writer, csvProperties, data);

        // BOTTOM
        boardDimensions.entrySet().stream().filter(entry -> entry.getKey() != BoardPosition.TOP).forEach(entry -> {
            data.clear();

            final Map<CSVColumn, Long> frameBoardEdges = switch (entry.getKey()) {
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
                default -> throw new InvalidParameterException("Unsupported board position: " + entry.getKey());
            };

            addPart(csvProperties,
                    boards,
                    edges,
                    materialNames,
                    item,
                    part,
                    csvProperties.nameDuplicatedFrameFormat(),
                    entry.getKey(),
                    boardDimensions.get(entry.getKey()),
                    false,
                    frameBoardEdges,
                    false,
                    data
            );

            printLine(writer, csvProperties, data);
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

    private void addPart(final CSVPropertiesData csvProperties,
                         final List<OrderBoardData> boards,
                         final List<OrderEdgeData> edges,
                         final Map<Long, String> materialNames,
                         final OrderItemDo item,
                         final PartData part,
                         final String nameFormat,
                         final BoardPosition position,
                         final DimensionsData dimensions,
                         final boolean orientation,
                         final Map<CSVColumn, Long> partEdges,
                         final boolean description,
                         final Map<CSVColumn, String> data
    ) {
        // NUMBER
        addNumber(csvProperties, item, position, data);
        // NAME
        addName(csvProperties, nameFormat, item, position, part.dimensions().get(position), data);
        // MATERIAL
        data.put(CSVColumn.MATERIAL, getMaterial(materialNames, part.boards().get(position)));
        // DECOR
        data.put(CSVColumn.DECOR, getDecor(boards, part.boards().get(position)));
        // DIMENSIONS
        addDimensions(dimensions, data);
        // QUANTITY
        data.put(CSVColumn.QUANTITY, toString(item.getQuantity()));
        // ORIENTATION
        data.put(CSVColumn.ORIENTATION, toString(orientation));
        // THICKNESS
        data.put(CSVColumn.THICKNESS, getThickness(boards, part.boards().get(position)));
        // EDGES
        addEdges(csvProperties, edges, partEdges, data);
        // CORNERS
        addCorners(csvProperties, edges, part, data);
        // DESCRIPTION
        if (description) {
            data.put(CSVColumn.DESCRIPTION, addQuotes(item.getDescription()));
        }
    }

    private void addNumber(final CSVPropertiesData csvProperties,
                           final OrderItemDo item,
                           final BoardPosition position,
                           final Map<CSVColumn, String> data) {
        data.put(CSVColumn.NUMBER,
                getNumber(csvProperties.numberFormat(), item.getSortNum() + 1, getPositionName(csvProperties, position)));
    }

    private void addName(final CSVPropertiesData csvProperties,
                         final String format,
                         final OrderItemDo item,
                         final BoardPosition position,
                         final DimensionsData dimensions,
                         final Map<CSVColumn, String> data
    ) {
        data.put(CSVColumn.NAME, getName(
                format,
                item.getName(),
                getPositionName(csvProperties, position),
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

    private void addEdges(final CSVPropertiesData csvProperties,
                          final List<OrderEdgeData> edges,
                          final Map<CSVColumn, Long> partEdges,
                          final Map<CSVColumn, String> data) {
        partEdges.forEach((key, value) -> data.put(key, getEdge(csvProperties.edgeFormat(), edges, value)));
    }

    private void addCorners(final CSVPropertiesData csvProperties, final List<OrderEdgeData> edges, final PartData part, final Map<CSVColumn, String> data) {
        part.corners().forEach((key, value) -> {
            data.put(switch (key) {
                        case A1B1 -> CSVColumn.CORNER_A1B1;
                        case A1B2 -> CSVColumn.CORNER_A1B2;
                        case A2B1 -> CSVColumn.CORNER_A2B1;
                        case A2B2 -> CSVColumn.CORNER_A2B2;
                    },
                    getCorner(csvProperties, edges, key, value)
            );
        });
    }

    private String getNumber(final String format, final int num, final String positionName) {
        return addQuotes(format.formatted(num, positionName));
    }

    private String getName(final String format,
                           final String itemName,
                           final String positionName,
                           final DimensionsData partDimensions,
                           final Integer quantity) {
        return addQuotes(format.formatted(itemName, positionName, partDimensions.x().intValue(), partDimensions.y().intValue(), quantity));
    }

    private String getMaterial(final Map<Long, String> materialNames, final long boardId) {
        return addQuotes(materialNames.getOrDefault(boardId, MATERIAL_NOT_FOUND));
    }

    private String getDecor(final List<OrderBoardData> boards, final long boardId) {
        final Optional<OrderBoardData> board = findBoard(boards, boardId);
        return addQuotes(board.map(OrderBoardData::boardCode).orElse(BOARD_NOT_FOUND) + " " +
                board.map(OrderBoardData::structureCode).orElse(BOARD_NOT_FOUND) + " " +
                board.map(OrderBoardData::name).orElse(BOARD_NOT_FOUND)
        );
    }

    private String getThickness(final List<OrderBoardData> boards, final long boardId) {
        final Optional<OrderBoardData> board = findBoard(boards, boardId);
        return board.map(OrderBoardData::thickness).map(BigDecimal::intValue).map(this::toString).orElse("");
    }

    private String getEdge(final String format, final List<OrderEdgeData> edges, final Long edgeId) {
        return addQuotes(edgeString(format, edges, edgeId));
    }

    private String edgeString(final String format, final List<OrderEdgeData> edges, final Long edgeId) {
        if (edgeId == null) {
            return "";
        }
        final Optional<OrderEdgeData> edge = findEdge(edges, edgeId);
        return format.formatted(
                edge.map(OrderEdgeData::code).orElse(EDGE_NOT_FOUND),
                edge.map(OrderEdgeData::width).map(BigDecimal::intValue).orElse(0),
                edge.map(OrderEdgeData::thickness).map(BigDecimal::doubleValue).orElse(0d)
        );
    }

    private String getCorner(final CSVPropertiesData csvProperties,
                             final List<OrderEdgeData> edges,
                             final CornerPosition position,
                             final PartCornerData partCorner) {
        if (partCorner == null) {
            return addQuotes("");
        }

        return addQuotes(cornerString(csvProperties, position, partCorner) + " " + edgeString(csvProperties.edgeFormat(), edges, partCorner.edgeId()));
    }

    private String cornerString(final CSVPropertiesData csvProperties, final CornerPosition position, final PartCornerData partCorner) {
        return switch (partCorner) {
            case final PartCornerStraightData partCornerStraightData -> csvProperties.cornerStraightFormat().formatted(
                    csvProperties.corners().getOrDefault(position, position.name()),
                    partCornerStraightData.dimensions().x().intValue(),
                    partCornerStraightData.dimensions().y().intValue()
            );
            case final PartCornerRoundedData partCornerRoundedData -> csvProperties.cornerStraightFormat().formatted(
                    csvProperties.corners().getOrDefault(position, position.name()),
                    partCornerRoundedData.radius().intValue()
            );
            default ->
                    throw new InvalidParameterException("Unsupported part corner type: " + partCorner.getClass().getSimpleName());
        };
    }

    private Optional<OrderBoardData> findBoard(final List<OrderBoardData> boards, final long boardId) {
        return boards.stream().filter(board -> board.id().equals(boardId)).findFirst();
    }

    private Optional<OrderEdgeData> findEdge(final List<OrderEdgeData> edges, final long edgeId) {
        return edges.stream().filter(edge -> edge.id().equals(edgeId)).findFirst();
    }

    private String getPositionName(final CSVPropertiesData csvProperties, final BoardPosition position) {
        return csvProperties.boards().getOrDefault(position, position.name());
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

    private void printLine(final PrintWriter writer, final CSVPropertiesData csvProperties, final Map<CSVColumn, String> data) {
        String s = Arrays.stream(CSVColumn.values())
                .map(key -> data.getOrDefault(key, ""))
                .map(scDf::toDf)
                .reduce("", (s1, s2) -> s1 + csvProperties.separator() + s2);

        for (final String regex : csvProperties.replacements().keySet()) {
            s = s.replaceAll(regex, csvProperties.replacements().get(regex));
        }

        writer.println(s);
    }
}
