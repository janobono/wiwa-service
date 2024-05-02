package sk.janobono.wiwa.business.impl.component;

import org.springframework.stereotype.Component;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.order.part.*;
import sk.janobono.wiwa.model.FrameType;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class CutLengthCalculationUtil extends BaseCalculationUtil {

    public Map<BigDecimal, BigDecimal> calculateBoardCutLength(final PartData part,
                                                               final Map<Long, BigDecimal> boardThickness,
                                                               final ManufacturePropertiesData manufactureProperties) {
        final Map<BigDecimal, BigDecimal> cutMap = new HashMap<>();

        switch (part) {
            case final PartBasicData basic -> {
                addDimensions(
                        boardThickness.get(basic.boardId()),
                        cutMap,
                        basic.dimensionsTOP()
                );
                for (final PartCornerData corner : basic.corners().values()) {
                    addValue(
                            boardThickness.get(basic.boardId()),
                            cutMap,
                            calculateCornerLength(corner)
                    );
                }
            }
            case final PartDuplicatedBasicData duplicatedBasic -> {
                addDimensions(
                        boardThickness.get(duplicatedBasic.boardId()),
                        cutMap,
                        duplicatedBasic.dimensionsTOP()
                                .add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO))
                );

                addDimensions(
                        boardThickness.get(duplicatedBasic.boardIdBottom()),
                        cutMap,
                        duplicatedBasic.dimensionsTOP()
                                .add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO))
                );

                addDimensions(
                        boardThickness.get(duplicatedBasic.boardId())
                                .add(boardThickness.get(duplicatedBasic.boardIdBottom())),
                        cutMap,
                        duplicatedBasic.dimensionsTOP()
                );

                for (final PartCornerData corner : duplicatedBasic.corners().values()) {
                    addValue(
                            boardThickness.get(duplicatedBasic.boardId())
                                    .add(boardThickness.get(duplicatedBasic.boardIdBottom())),
                            cutMap,
                            calculateCornerLength(corner)
                    );
                }
            }
            case final PartFrameData frame -> {
                for (final Map.Entry<BoardPosition, Long> boardEntry : frame.boards().entrySet()) {
                    final BigDecimal thickness = boardThickness.get(boardEntry.getValue());
                    addDimensions(thickness, cutMap, part.dimensions().get(boardEntry.getKey()));
                }
            }
            case final PartDuplicatedFrameData duplicatedFrame -> {
                calculateDuplicatedFrameCutLength(duplicatedFrame, boardThickness, manufactureProperties, cutMap);
            }
            default -> throw new InvalidParameterException("Unsupported part type: " + part.getClass().getSimpleName());
        }

        return cutMap;
    }

    private void calculateDuplicatedFrameCutLength(final PartDuplicatedFrameData duplicatedFrame,
                                                   final Map<Long, BigDecimal> boardThickness,
                                                   final ManufacturePropertiesData manufactureProperties,
                                                   final Map<BigDecimal, BigDecimal> cutMap) {
        final BigDecimal topLayerThickness = boardThickness.get(duplicatedFrame.boardId());
        final BigDecimal bottomLayerThickness = boardThickness.get(duplicatedFrame.boards().entrySet().stream()
                .filter(entry -> switch (entry.getKey()) {
                    case A1, A2, B1, B2 -> true;
                    default -> false;
                })
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(-1L));

        for (final BoardPosition boardPosition : duplicatedFrame.boards().keySet()) {
            final DimensionsData boardDimensions = duplicatedFrame.dimensions().get(boardPosition);
            switch (boardPosition) {
                case TOP -> addDimensions(topLayerThickness, cutMap,
                        calculateTopBoardDimensions(duplicatedFrame, manufactureProperties, boardDimensions)
                );
                case A1, A2 -> addDimensions(bottomLayerThickness, cutMap,
                        calculateADimensions(duplicatedFrame, manufactureProperties, boardDimensions)
                );
                case B1, B2 -> addDimensions(bottomLayerThickness, cutMap,
                        calculateBDimensions(duplicatedFrame, manufactureProperties, boardDimensions)
                );
                default -> throw new InvalidParameterException("Unsupported board position: " + boardPosition);
            }
        }
        calculateFinalAndCornerLengths(duplicatedFrame, topLayerThickness, bottomLayerThickness, cutMap);
    }

    private DimensionsData calculateADimensions(final PartDuplicatedFrameData duplicatedFrame,
                                                final ManufacturePropertiesData manufactureProperties,
                                                final DimensionsData boardDimensions) {
        if (duplicatedFrame.frameType() == FrameType.VERTICAL) {
            BigDecimal x = boardDimensions.x();
            if (!duplicatedFrame.boards().containsKey(BoardPosition.B1)) {
                x = x.add(manufactureProperties.duplicatedBoardAppend());
            }
            if (!duplicatedFrame.boards().containsKey(BoardPosition.B2)) {
                x = x.add(manufactureProperties.duplicatedBoardAppend());
            }
            return new DimensionsData(
                    x,
                    boardDimensions.y().add(manufactureProperties.duplicatedBoardAppend())
            );
        } else {
            return new DimensionsData(
                    boardDimensions.x().add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO)),
                    boardDimensions.y().add(manufactureProperties.duplicatedBoardAppend())
            );
        }
    }

    private DimensionsData calculateBDimensions(final PartDuplicatedFrameData duplicatedFrame,
                                                final ManufacturePropertiesData manufactureProperties,
                                                final DimensionsData boardDimensions) {
        if (duplicatedFrame.frameType() == FrameType.VERTICAL) {
            return new DimensionsData(
                    boardDimensions.x().add(manufactureProperties.duplicatedBoardAppend()),
                    boardDimensions.y().add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO))
            );
        } else {
            BigDecimal y = boardDimensions.y();
            if (!duplicatedFrame.boards().containsKey(BoardPosition.A1)) {
                y = y.add(manufactureProperties.duplicatedBoardAppend());
            }
            if (!duplicatedFrame.boards().containsKey(BoardPosition.A2)) {
                y = y.add(manufactureProperties.duplicatedBoardAppend());
            }
            return new DimensionsData(
                    boardDimensions.x().add(manufactureProperties.duplicatedBoardAppend()),
                    y
            );
        }
    }

    private DimensionsData calculateTopBoardDimensions(final PartDuplicatedFrameData duplicatedFrame,
                                                       final ManufacturePropertiesData manufactureProperties,
                                                       final DimensionsData boardDimensions) {
        if (duplicatedFrame.boards().size() > 2) {
            return boardDimensions.add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO));
        } else {
            if (duplicatedFrame.boards().containsKey(BoardPosition.A1) || duplicatedFrame.boards().containsKey(BoardPosition.A2)) {
                return new DimensionsData(
                        boardDimensions.x()
                                .add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO)),
                        boardDimensions.y()
                                .add(manufactureProperties.duplicatedBoardAppend())
                );
            } else {
                return new DimensionsData(
                        boardDimensions.x()
                                .add(manufactureProperties.duplicatedBoardAppend()),
                        boardDimensions.y()
                                .add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO))
                );
            }
        }
    }

    private void calculateFinalAndCornerLengths(final PartDuplicatedFrameData duplicatedFrame,
                                                final BigDecimal topLayerThickness,
                                                final BigDecimal bottomLayerThickness,
                                                final Map<BigDecimal, BigDecimal> cutMap) {
        // A1, A2
        for (final BoardPosition boardPosition : Set.of(BoardPosition.A1, BoardPosition.A2)) {
            final BigDecimal finalLength = calculateFinalXLength(duplicatedFrame,
                    duplicatedFrame.dimensionsTOP().x(), boardPosition);
            final BigDecimal topLength = duplicatedFrame.dimensionsTOP().x().subtract(finalLength);
            addValue(topLayerThickness.add(bottomLayerThickness), cutMap, finalLength);
            addValue(topLayerThickness, cutMap, topLength);
        }
        // B1, B2
        for (final BoardPosition boardPosition : Set.of(BoardPosition.B1, BoardPosition.B2)) {
            final BigDecimal finalLength = calculateFinalYLength(duplicatedFrame,
                    duplicatedFrame.dimensionsTOP().y(), boardPosition);
            final BigDecimal topLength = duplicatedFrame.dimensionsTOP().y().subtract(finalLength);
            addValue(topLayerThickness.add(bottomLayerThickness), cutMap, finalLength);
            addValue(topLayerThickness, cutMap, topLength);
        }

        // CORNERS
        duplicatedFrame.corners().forEach((cornerPosition, cornerData) -> {
            if (isCornerFinal(duplicatedFrame, cornerPosition)) {
                addValue(topLayerThickness.add(bottomLayerThickness), cutMap, calculateCornerLength(cornerData));
            } else {
                addValue(topLayerThickness, cutMap, calculateCornerLength(cornerData));
            }
        });
    }

    private boolean isCornerFinal(final PartDuplicatedFrameData duplicatedFrame, final CornerPosition cornerPosition) {
        return switch (cornerPosition) {
            case A1B1 ->
                    duplicatedFrame.boards().containsKey(BoardPosition.A1) || duplicatedFrame.boards().containsKey(BoardPosition.B1);
            case A1B2 ->
                    duplicatedFrame.boards().containsKey(BoardPosition.A1) || duplicatedFrame.boards().containsKey(BoardPosition.B2);
            case A2B1 ->
                    duplicatedFrame.boards().containsKey(BoardPosition.A2) || duplicatedFrame.boards().containsKey(BoardPosition.B1);
            case A2B2 ->
                    duplicatedFrame.boards().containsKey(BoardPosition.A2) || duplicatedFrame.boards().containsKey(BoardPosition.B2);
        };
    }

    private BigDecimal calculateFinalXLength(final PartDuplicatedFrameData duplicatedFrame,
                                             final BigDecimal x,
                                             final BoardPosition boardPosition) {
        if (duplicatedFrame.boards().containsKey(boardPosition)) {
            return x;
        }
        final DimensionsData zero = new DimensionsData(BigDecimal.ZERO, BigDecimal.ZERO);
        return duplicatedFrame.dimensions().getOrDefault(BoardPosition.B1, zero).y()
                .add(duplicatedFrame.dimensions().getOrDefault(BoardPosition.B2, zero).y());
    }

    private BigDecimal calculateFinalYLength(final PartDuplicatedFrameData duplicatedFrame,
                                             final BigDecimal y,
                                             final BoardPosition boardPosition) {
        if (duplicatedFrame.boards().containsKey(boardPosition)) {
            return y;
        }
        final DimensionsData zero = new DimensionsData(BigDecimal.ZERO, BigDecimal.ZERO);
        return duplicatedFrame.dimensions().getOrDefault(BoardPosition.A1, zero).x()
                .add(duplicatedFrame.dimensions().getOrDefault(BoardPosition.A2, zero).x());
    }

    private void addDimensions(final BigDecimal thickness,
                               final Map<BigDecimal, BigDecimal> cutMap,
                               final DimensionsData dimensionsData) {
        final BigDecimal length = cutMap.getOrDefault(thickness, BigDecimal.ZERO);
        cutMap.put(thickness, length.add(calculatePerimeter(dimensionsData)));
    }

    private void addValue(final BigDecimal thickness,
                          final Map<BigDecimal, BigDecimal> cutMap,
                          final BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        final BigDecimal length = cutMap.getOrDefault(thickness, BigDecimal.ZERO);
        cutMap.put(thickness, length.add(millimeterToMeter(value)));
    }
}
