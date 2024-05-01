package sk.janobono.wiwa.business.impl.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.order.part.BoardPosition;
import sk.janobono.wiwa.business.model.order.part.PartData;
import sk.janobono.wiwa.business.model.order.part.PartDuplicatedBasicData;
import sk.janobono.wiwa.business.model.order.part.PartDuplicatedFrameData;
import sk.janobono.wiwa.model.FrameType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BoardAreaCalculationUtil {

    private static final short PRECISION = 6;

    public Map<BoardPosition, BigDecimal> calculateArea(final PartData part,
                                                        final ManufacturePropertiesData manufactureProperties) {
        return switch (part) {
            case final PartDuplicatedBasicData duplicatedBasic -> duplicatedBasic.boards().keySet().stream()
                    .map(position -> new AbstractMap.SimpleEntry<>(position, duplicatedBasic.dimensions().get(position)))
                    .map(entry -> {
                        final DimensionsData boardDimensions = entry.getValue()
                                .add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO));
                        return new AbstractMap.SimpleEntry<>(entry.getKey(), countArea(boardDimensions));
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            case final PartDuplicatedFrameData duplicatedFrame -> duplicatedFrame.boards().keySet().stream()
                    .map(position -> new AbstractMap.SimpleEntry<>(position, duplicatedFrame.dimensions().get(position)))
                    .map(entry -> {
                        final DimensionsData boardDimensions = getDimensions(
                                duplicatedFrame,
                                entry.getKey(),
                                entry.getValue(),
                                manufactureProperties);
                        return new AbstractMap.SimpleEntry<>(entry.getKey(), countArea(boardDimensions));
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            default -> part.boards().keySet().stream()
                    .map(boardPosition -> new AbstractMap.SimpleEntry<>(boardPosition, part.dimensions().get(boardPosition)))
                    .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), countArea(entry.getValue())))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        };
    }

    private DimensionsData getDimensions(final PartDuplicatedFrameData part,
                                         final BoardPosition boardPosition,
                                         final DimensionsData boardDimensions,
                                         final ManufacturePropertiesData manufactureProperties) {
        return switch (boardPosition) {
            case A1, A2 -> {
                if (part.frameType() == FrameType.VERTICAL) {
                    BigDecimal x = boardDimensions.x();
                    if (!part.boards().containsKey(BoardPosition.B1)) {
                        x = x.add(manufactureProperties.duplicatedBoardAppend());
                    }
                    if (!part.boards().containsKey(BoardPosition.B2)) {
                        x = x.add(manufactureProperties.duplicatedBoardAppend());
                    }
                    yield new DimensionsData(
                            x,
                            boardDimensions.y().add(manufactureProperties.duplicatedBoardAppend())
                    );
                } else {
                    yield new DimensionsData(
                            boardDimensions.x().add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO)),
                            boardDimensions.y().add(manufactureProperties.duplicatedBoardAppend())
                    );
                }
            }
            case B1, B2 -> {
                if (part.frameType() == FrameType.VERTICAL) {
                    yield new DimensionsData(
                            boardDimensions.x().add(manufactureProperties.duplicatedBoardAppend()),
                            boardDimensions.y().add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO))
                    );
                } else {
                    BigDecimal y = boardDimensions.y();
                    if (!part.boards().containsKey(BoardPosition.A1)) {
                        y = y.add(manufactureProperties.duplicatedBoardAppend());
                    }
                    if (!part.boards().containsKey(BoardPosition.A2)) {
                        y = y.add(manufactureProperties.duplicatedBoardAppend());
                    }
                    yield new DimensionsData(
                            boardDimensions.x().add(manufactureProperties.duplicatedBoardAppend()),
                            y
                    );
                }
            }
            default -> {
                if (part.boards().size() > 2) {
                    yield boardDimensions.add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO));
                } else {
                    if (part.boards().containsKey(BoardPosition.A1) || part.boards().containsKey(BoardPosition.A2)) {
                        yield new DimensionsData(
                                boardDimensions.x()
                                        .add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO)),
                                boardDimensions.y()
                                        .add(manufactureProperties.duplicatedBoardAppend())
                        );
                    } else {
                        yield new DimensionsData(
                                boardDimensions.x()
                                        .add(manufactureProperties.duplicatedBoardAppend()),
                                boardDimensions.y()
                                        .add(manufactureProperties.duplicatedBoardAppend().multiply(BigDecimal.TWO))
                        );
                    }
                }
            }
        };
    }

    private BigDecimal millimeterToMeter(final BigDecimal value) {
        return value.divide(BigDecimal.valueOf(1000), PRECISION, RoundingMode.HALF_UP);
    }

    private BigDecimal countArea(final DimensionsData dimensions) {
        return millimeterToMeter(dimensions.x())
                .multiply(millimeterToMeter(dimensions.y()))
                .setScale(PRECISION, RoundingMode.HALF_UP);
    }
}
