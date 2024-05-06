package sk.janobono.wiwa.business.impl.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sk.janobono.wiwa.business.impl.component.DataUtil;
import sk.janobono.wiwa.business.impl.component.MaterialUtil;
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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.*;

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
                printHeader(writer, csvProperties);
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

    private void printHeader(final PrintWriter writer, final CSVPropertiesData csvProperties) {
        writer.println(
                Arrays.stream(CSVColumn.values())
                        .map(col -> csvProperties.columns().getOrDefault(col, col.name()))
                        .map(this::normalize)
                        .map(this::putIntoQuotes)
                        .reduce("", (s1, s2) -> s1 + csvProperties.separator() + s2)
        );
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
                    printBasic(writer, manufactureProperties, csvProperties, boards, edges, materialNames, item, partBasic);
            case final PartDuplicatedBasicData partDuplicatedBasic ->
                    printDuplicatedBasic(writer, manufactureProperties, csvProperties, boards, edges, materialNames, item, partDuplicatedBasic);
            case final PartFrameData partFrame ->
                    printFrame(writer, manufactureProperties, csvProperties, boards, edges, materialNames, item, partFrame);
            case final PartDuplicatedFrameData partDuplicatedFrame ->
                    printPartDuplicatedFrame(writer, manufactureProperties, csvProperties, boards, edges, materialNames, item, partDuplicatedFrame);
            default -> throw new InvalidParameterException("Unsupported part type: " + part.getClass().getSimpleName());
        }
    }

//"P.C";"NAZOV_DIELCA";"MATERIAL";"DEKOR";"ROZMER_A";"ROZMER_B";"MNOZSTVO";"SMER_VLAKIEN";"HRUBKA";"ROZMER_A_1_hrana";"ROZMER_A_2_hrana";"ROZMER_B_1_hrana";"ROZMER_B_2_hrana";"POZNAMKA"
//"1";"basic";"Laminovane_DTD_KASTAMONU";"D102_PS30_Bezova";"100";"100";"1";"0";"18";"N160B_22x08";"N160B_22x08";"N160B_22x08";"N160B_22x08";""
//"2";"duplicated_ZDV._200x200_mm_2_ks_";"Laminovane_DTD_KASTAMONU";"D102_PS30_Bezova";"220";"220";"4";"0";"18";"N160B_42x2";"N160B_42x2";"N160B_42x2";"N160B_42x2";""
//"3";"duplicated_frame_ZDV._vlys_500x500_mm_3_ks_";"Laminovane_DTD_KASTAMONU";"D102_PS30_Bezova";"520";"520";"3";"0";"18";"N160B_42x2";"N160B_42x2";"N160B_42x2";"N160B_42x2";""
//"3_A1";"duplicated_frame_ZDV._vlys_A_1_500x500_mm_3_ks_";"Laminovane_DTD_KASTAMONU";"D102_PS30_Bezova";"520";"110";"3";"0";"18";"";"";"";"";""
//"3_A2";"duplicated_frame_ZDV._vlys_A_2_500x500_mm_3_ks_";"Laminovane_DTD_KASTAMONU";"D102_PS30_Bezova";"520";"110";"3";"0";"18";"";"";"";"";""
//"3_B1";"duplicated_frame_ZDV._vlys_B_1_500x500_mm_3_ks_";"Laminovane_DTD_KASTAMONU";"D102_PS30_Bezova";"110";"300";"3";"0";"18";"";"";"";"";""
//"3_B2";"duplicated_frame_ZDV._vlys_B_2_500x500_mm_3_ks_";"Laminovane_DTD_KASTAMONU";"D102_PS30_Bezova";"110";"300";"3";"0";"18";"";"";"";"";""

    private void printBasic(final PrintWriter writer,
                            final ManufacturePropertiesData manufactureProperties,
                            final CSVPropertiesData csvProperties,
                            final List<OrderBoardData> boards,
                            final List<OrderEdgeData> edges,
                            final Map<Long, String> materialNames,
                            final OrderItemDo item,
                            final PartBasicData partBasic) {
        final List<String> line = new ArrayList<>();
        // NUMBER
//        line.add(getNumber(csvProperties, item, BoardPosition.TOP));
        // NAME

        // MATERIAL

        // DECOR

        // X_DIMENSION

        // Y_DIMENSION

        // QUANTITY

        // ORIENTATION

        // THICKNESS

        // EDGE_A1

        // EDGE_A2

        // EDGE_B1

        // EDGE_B2

        // CORNER_A1B1

        // CORNER_A1B2

        // CORNER_A2B1

        // CORNER_A2B2

        // DESCRIPTION



//        // NUMBER
//        line.add(getNumber(item));
//        // NAME
//        line.add(putIntoQuotes(normalize(getName(csvProperties.nameBasicFormat(), item.getName(), BoardPosition.TOP, partBasic, item.getQuantity()))));
//        // MATERIAL
//        line.add(getMaterial(materialNames, partBasic.boardId()));
//        // CODES
//        line.add(getCodes(boards, partBasic.boardId()));
//        // X_DIMENSION
//        line.add(toString(partBasic.dimensionsTOP().x()));
//        // Y_DIMENSION
//        line.add(toString(partBasic.dimensionsTOP().y()));
//        // QUANTITY
//        line.add(toString(item.getQuantity()));
//        // ORIENTATION
//        line.add(toString(item.getOrientation()));
//        // THICKNESS
//        line.add(getThickness(boards, partBasic.boardId()));
//        // EDGE_A1
//        line.add(getBorderText(item.borderIds[0]));
//        // EDGE_A2
//        line.add(getBorderText(item.borderIds[0]));
//        // EDGE_B1
//        line.add(getBorderText(item.borderIds[0]));
//        // EDGE_B2
//        line.add(getBorderText(item.borderIds[0]));
//        // DESCRIPTION
//        line.add(normalize(item.getDescription()));
//        printLine(writer, csvProperties.separator(), line);
    }

    private void printDuplicatedBasic(final PrintWriter writer,
                                      final ManufacturePropertiesData manufactureProperties,
                                      final CSVPropertiesData csvProperties,
                                      final List<OrderBoardData> boards,
                                      final List<OrderEdgeData> edges,
                                      final Map<Long, String> producerNames,
                                      final OrderItemDo item,
                                      final PartDuplicatedBasicData partDuplicatedBasic) {
// TODO
    }

    private void printFrame(final PrintWriter writer,
                            final ManufacturePropertiesData manufactureProperties,
                            final CSVPropertiesData csvProperties,
                            final List<OrderBoardData> boards,
                            final List<OrderEdgeData> edges,
                            final Map<Long, String> producerNames,
                            final OrderItemDo item,
                            final PartFrameData partFrame) {
        // TODO
    }

    private void printPartDuplicatedFrame(final PrintWriter writer,
                                          final ManufacturePropertiesData manufactureProperties,
                                          final CSVPropertiesData csvProperties,
                                          final List<OrderBoardData> boards,
                                          final List<OrderEdgeData> edges,
                                          final Map<Long, String> producerNames,
                                          final OrderItemDo item,
                                          final PartDuplicatedFrameData partDuplicatedFrame) {
        // TODO

    }

//    private String getName(final String format,
//                           final String itemName,
//                           final BoardPosition position,
//                           final PartData part,
//                           final Integer quantity){
//        return format.formatted(
//                itemName,
//        );
//        // %s (basic %s - %s x %s mm - %s p)
//    }

    private String getNumber(final OrderItemDo item) {
        return Integer.toString(item.getSortNum() + 1);
    }

    private String getMaterial(final Map<Long, String> materialNames, final long boardId) {
        return putIntoQuotes(normalize(materialNames.get(boardId)));
    }
//
//    private String getCodes(final List<OrderBoardData> boards, final PartData part, final) {
//        final OrderBoardData board = findBoard(boards, boardId);
//        return putIntoQuotes(normalize(board.boardCode() + "_" + board.structureCode() + "_" + board.name()));
//    }
//
//    private String getThickness(final List<OrderBoardData> boards, final long boardId) {
//        final OrderBoardData board = findBoard(boards, boardId);
//        return toString(board.thickness());
//    }

    private String normalize(final String s) {
        return Optional.ofNullable(s)
                .map(scDf::toDf)
                .orElse("")
                .replaceAll("<.*?>", "")
                .replaceAll("\\s+", "_");
    }

    private String putIntoQuotes(final String s) {
        return Optional.ofNullable(s).map(d -> "\"" + d + "\"").orElse("\"\"");
    }

    private String toString(final Boolean b) {
        return Optional.ofNullable(b).map(v -> v ? "1" : "0").orElse("0");
    }

    private String toString(final Integer i) {
        return Optional.ofNullable(i).map(Object::toString).orElse("0");
    }

    private String toString(final BigDecimal b) {
        return Optional.ofNullable(b)
                .map(v -> v.setScale(0, RoundingMode.HALF_UP))
                .map(BigDecimal::toPlainString)
                .orElse("0");
    }

    private void printLine(final PrintWriter writer, final String separator, final List<String> line) {
        writer.println(line.stream().reduce("", (s1, s2) -> s1 + separator + s2));
    }

    private Optional<OrderBoardData> findBoard(final List<OrderBoardData> boards, final long id) {
        return boards.stream()
                .filter(b -> b.id() == id)
                .findFirst();
    }
}
