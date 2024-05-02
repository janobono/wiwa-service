package sk.janobono.wiwa.business.impl.component;

import org.springframework.stereotype.Component;
import sk.janobono.wiwa.business.impl.model.summary.EdgeLengthData;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.order.part.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class EdgeLengthCalculationUtil extends BaseCalculationUtil {

    public Map<Long, EdgeLengthData> calculateEdgeLength(final PartData part, final ManufacturePropertiesData manufactureProperties) {
        final Map<Long, EdgeLengthData> edgeLengthMap = new HashMap<>();
        final Map<BoardPosition, DimensionsData> dimensions = part.dimensions();

        for (final Map.Entry<EdgePosition, Long> edgeEntry : part.edges().entrySet()) {
            final Long edgeId = edgeEntry.getValue();
            final EdgeLengthData edgeLength = edgeLengthMap.getOrDefault(edgeId, new EdgeLengthData(BigDecimal.ZERO, BigDecimal.ZERO));
            edgeLengthMap.put(edgeId, switch (edgeEntry.getKey()) {
                case A1, A2 ->
                        calculateEdgeLength(edgeLength, dimensions.get(BoardPosition.TOP).x(), manufactureProperties);
                case B1, B2 ->
                        calculateEdgeLength(edgeLength, dimensions.get(BoardPosition.TOP).y(), manufactureProperties);
                case A1I ->
                        calculateEdgeLength(edgeLength, dimensions.get(BoardPosition.A1).x(), manufactureProperties);
                case A2I ->
                        calculateEdgeLength(edgeLength, dimensions.get(BoardPosition.A2).x(), manufactureProperties);
                case B1I ->
                        calculateEdgeLength(edgeLength, dimensions.get(BoardPosition.B1).y(), manufactureProperties);
                case B2I ->
                        calculateEdgeLength(edgeLength, dimensions.get(BoardPosition.B2).y(), manufactureProperties);
                case A1B1 ->
                        calculateEdgeLength(edgeLength, part.corners().get(CornerPosition.A1B1), manufactureProperties);
                case A1B2 ->
                        calculateEdgeLength(edgeLength, part.corners().get(CornerPosition.A1B2), manufactureProperties);
                case A2B1 ->
                        calculateEdgeLength(edgeLength, part.corners().get(CornerPosition.A2B1), manufactureProperties);
                case A2B2 ->
                        calculateEdgeLength(edgeLength, part.corners().get(CornerPosition.A2B2), manufactureProperties);
            });
        }

        return edgeLengthMap;
    }

    private EdgeLengthData calculateEdgeLength(final EdgeLengthData edgeLength,
                                               final BigDecimal augend,
                                               final ManufacturePropertiesData manufactureProperties) {
        return new EdgeLengthData(
                calculateLength(edgeLength, augend),
                calculateConsumption(edgeLength, augend, manufactureProperties)
        );
    }

    private EdgeLengthData calculateEdgeLength(final EdgeLengthData edgeLength,
                                               final PartCornerData partCorner,
                                               final ManufacturePropertiesData manufactureProperties) {
        final BigDecimal augend = calculateCornerLength(partCorner);

        return new EdgeLengthData(
                calculateLength(edgeLength, augend),
                calculateConsumption(edgeLength, augend, manufactureProperties)
        );
    }

    private BigDecimal calculateLength(final EdgeLengthData edgeLength, final BigDecimal augend) {
        return edgeLength.length().add(millimeterToMeter(augend));
    }

    private BigDecimal calculateConsumption(final EdgeLengthData edgeLength,
                                            final BigDecimal augend,
                                            final ManufacturePropertiesData manufactureProperties) {
        return edgeLength.consumption().add(millimeterToMeter(augend.add(manufactureProperties.edgeLengthAppend())));
    }
}
