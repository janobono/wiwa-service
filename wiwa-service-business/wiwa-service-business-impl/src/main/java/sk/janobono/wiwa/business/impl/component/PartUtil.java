package sk.janobono.wiwa.business.impl.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.business.impl.model.part.CornerPosition;
import sk.janobono.wiwa.business.model.application.ManufactureDimensionsData;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.business.model.order.part.*;
import sk.janobono.wiwa.exception.WiwaException;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.*;

@RequiredArgsConstructor
@Component
public class PartUtil {

    public void validate(final PartData part,
                         final Map<Long, OrderBoardData> boards,
                         final Map<Long, OrderEdgeData> edges,
                         final ManufacturePropertiesData manufactureProperties) {
        switch (part) {
            case final PartBasicData partBasic -> validateBasic(partBasic, boards, edges, manufactureProperties);
            case final PartFrameData partFrame -> validateFrame(partFrame, boards, edges, manufactureProperties);
            case final PartDuplicatedBasicData partDuplicatedBasic ->
                    validateDuplicatedBasic(partDuplicatedBasic, boards, edges, manufactureProperties);
            case final PartDuplicatedFrameData partDuplicatedFrame ->
                    validatePartDuplicatedFrame(partDuplicatedFrame, boards, edges, manufactureProperties);
            default -> throw new InvalidParameterException("Unsupported part type: " + part.getClass().getSimpleName());
        }
    }

    public Set<Long> getBoardIds(final PartData part) {
        return switch (part) {
            case final PartBasicData partBasic -> getBasicBoardIds(partBasic);
            case final PartFrameData partFrame -> getFrameBoardIds(partFrame);
            case final PartDuplicatedBasicData partDuplicatedBasic -> getDuplicatedBasicBoardIds(partDuplicatedBasic);
            case final PartDuplicatedFrameData partDuplicatedFrame ->
                    getPartDuplicatedFrameBoardIds(partDuplicatedFrame);
            default -> throw new InvalidParameterException("Unsupported part type: " + part.getClass().getSimpleName());
        };
    }

    public Set<Long> getEdgeIds(final PartData part) {
        return switch (part) {
            case final PartBasicData partBasic -> getBasicEdgeIds(partBasic);
            case final PartFrameData partFrame -> getFrameEdgeIds(partFrame);
            case final PartDuplicatedBasicData partDuplicatedBasic -> getDuplicatedBasicEdgeIds(partDuplicatedBasic);
            case final PartDuplicatedFrameData partDuplicatedFrame ->
                    getPartDuplicatedFrameEdgeIds(partDuplicatedFrame);
            default -> throw new InvalidParameterException("Unsupported part type: " + part.getClass().getSimpleName());
        };
    }

    private void validateBasic(final PartBasicData part,
                               final Map<Long, OrderBoardData> boards,
                               final Map<Long, OrderEdgeData> edges,
                               final ManufacturePropertiesData manufactureProperties) {
        checkDimensions(part.dimensionA(), part.dimensionB(), part.boardId(), boards, manufactureProperties);

        final Map<CornerPosition, ManufactureDimensionsData> cornerDimensions = new HashMap<>();
        Optional.ofNullable(part.cornerA1B1()).ifPresent(cornerData -> cornerDimensions.put(CornerPosition.A1B1, getCornerDimensions(cornerData)));
        Optional.ofNullable(part.cornerA1B2()).ifPresent(cornerData -> cornerDimensions.put(CornerPosition.A1B2, getCornerDimensions(cornerData)));
        Optional.ofNullable(part.cornerA2B1()).ifPresent(cornerData -> cornerDimensions.put(CornerPosition.A2B1, getCornerDimensions(cornerData)));
        Optional.ofNullable(part.cornerA2B2()).ifPresent(cornerData -> cornerDimensions.put(CornerPosition.A2B2, getCornerDimensions(cornerData)));
        checkCorners(part.dimensionA(), part.dimensionB(), cornerDimensions);

        checkEdges(boards.get(part.boardId()).thickness(), getEdgeIds(part), edges, manufactureProperties);
    }

    private void validateFrame(final PartFrameData part,
                               final Map<Long, OrderBoardData> boards,
                               final Map<Long, OrderEdgeData> edges,
                               final ManufacturePropertiesData manufactureProperties) {

       
        // TODO


        // TODO
    }

    private void validateDuplicatedBasic(final PartDuplicatedBasicData partDuplicatedBasic,
                                         final Map<Long, OrderBoardData> boards,
                                         final Map<Long, OrderEdgeData> edges,
                                         final ManufacturePropertiesData manufactureProperties) {
        // TODO
    }

    private void validatePartDuplicatedFrame(final PartDuplicatedFrameData partDuplicatedFrame,
                                             final Map<Long, OrderBoardData> boards,
                                             final Map<Long, OrderEdgeData> edges,
                                             final ManufacturePropertiesData manufactureProperties) {
        // TODO
    }

    private Set<Long> getBasicBoardIds(final PartBasicData partBasic) {
        return Set.of(partBasic.boardId());
    }

    private Set<Long> getFrameBoardIds(final PartFrameData partFrame) {
        return Set.of(partFrame.boardIdA1(), partFrame.boardIdA2(), partFrame.boardIdB1(), partFrame.boardIdB2());
    }

    private Set<Long> getDuplicatedBasicBoardIds(final PartDuplicatedBasicData partDuplicatedBasic) {
        return Set.of(partDuplicatedBasic.boardIdTop(), partDuplicatedBasic.boardIdBottom());
    }

    private Set<Long> getPartDuplicatedFrameBoardIds(final PartDuplicatedFrameData partDuplicatedFrame) {
        final Set<Long> ids = new HashSet<>();
        ids.add(partDuplicatedFrame.boardIdTop());
        Optional.ofNullable(partDuplicatedFrame.boardIdA1Bottom()).ifPresent(ids::add);
        Optional.ofNullable(partDuplicatedFrame.boardIdA2Bottom()).ifPresent(ids::add);
        Optional.ofNullable(partDuplicatedFrame.boardIdB1Bottom()).ifPresent(ids::add);
        Optional.ofNullable(partDuplicatedFrame.boardIdB2Bottom()).ifPresent(ids::add);
        return ids;
    }

    private Set<Long> getBasicEdgeIds(final PartBasicData partBasic) {
        final Set<Long> ids = new HashSet<>();
        Optional.ofNullable(partBasic.edgeIdA1()).ifPresent(ids::add);
        Optional.ofNullable(partBasic.edgeIdA2()).ifPresent(ids::add);
        Optional.ofNullable(partBasic.edgeIdB1()).ifPresent(ids::add);
        Optional.ofNullable(partBasic.edgeIdB2()).ifPresent(ids::add);
        return ids;
    }

    private Set<Long> getFrameEdgeIds(final PartFrameData partFrame) {
        final Set<Long> ids = new HashSet<>();
        Optional.ofNullable(partFrame.edgeIdA1()).ifPresent(ids::add);
        Optional.ofNullable(partFrame.edgeIdA1I()).ifPresent(ids::add);
        Optional.ofNullable(partFrame.edgeIdA2()).ifPresent(ids::add);
        Optional.ofNullable(partFrame.edgeIdA2I()).ifPresent(ids::add);
        Optional.ofNullable(partFrame.edgeIdB1()).ifPresent(ids::add);
        Optional.ofNullable(partFrame.edgeIdB1I()).ifPresent(ids::add);
        Optional.ofNullable(partFrame.edgeIdB2()).ifPresent(ids::add);
        Optional.ofNullable(partFrame.edgeIdB2I()).ifPresent(ids::add);
        return ids;
    }

    private Set<Long> getDuplicatedBasicEdgeIds(final PartDuplicatedBasicData partDuplicatedBasic) {
        final Set<Long> ids = new HashSet<>();
        Optional.ofNullable(partDuplicatedBasic.edgeIdA1()).ifPresent(ids::add);
        Optional.ofNullable(partDuplicatedBasic.edgeIdA2()).ifPresent(ids::add);
        Optional.ofNullable(partDuplicatedBasic.edgeIdB1()).ifPresent(ids::add);
        Optional.ofNullable(partDuplicatedBasic.edgeIdB2()).ifPresent(ids::add);
        return ids;
    }

    private Set<Long> getPartDuplicatedFrameEdgeIds(final PartDuplicatedFrameData partDuplicatedFrame) {
        final Set<Long> ids = new HashSet<>();
        Optional.ofNullable(partDuplicatedFrame.edgeIdA1()).ifPresent(ids::add);
        Optional.ofNullable(partDuplicatedFrame.edgeIdA1IBottom()).ifPresent(ids::add);
        Optional.ofNullable(partDuplicatedFrame.edgeIdA2()).ifPresent(ids::add);
        Optional.ofNullable(partDuplicatedFrame.edgeIdA2IBottom()).ifPresent(ids::add);
        Optional.ofNullable(partDuplicatedFrame.edgeIdB1()).ifPresent(ids::add);
        Optional.ofNullable(partDuplicatedFrame.edgeIdB1IBottom()).ifPresent(ids::add);
        Optional.ofNullable(partDuplicatedFrame.edgeIdB2()).ifPresent(ids::add);
        Optional.ofNullable(partDuplicatedFrame.edgeIdB2IBottom()).ifPresent(ids::add);
        return ids;
    }

    private void checkDimensions(final BigDecimal x,
                                 final BigDecimal y,
                                 final long boardId,
                                 final Map<Long, OrderBoardData> boards,
                                 final ManufacturePropertiesData manufactureProperties) {
        final ManufactureDimensionsData max = getMax(boardId, boards);
        checkMax(x, y, max);
        checkMin(x, y, manufactureProperties.minimalSystemDimensions());
    }

    private void checkMax(final BigDecimal x, final BigDecimal y, final ManufactureDimensionsData max) {
        final boolean horizontalIsOk = x.doubleValue() <= max.x().doubleValue() && y.doubleValue() <= max.y().doubleValue();
        final boolean verticalIsOk = x.doubleValue() <= max.y().doubleValue() && y.doubleValue() <= max.x().doubleValue();

        if (horizontalIsOk || verticalIsOk) {
            return;
        }

        throw WiwaException.ORDER_ITEM_PART_INVALID.exception("Invalid dimensions [{0},{1}] maximum is {2}", x, y, max);
    }

    private void checkMin(final BigDecimal x, final BigDecimal y, final ManufactureDimensionsData min) {
        final boolean horizontalIsOk = x.doubleValue() >= min.x().doubleValue() && y.doubleValue() >= min.y().doubleValue();
        final boolean verticalIsOk = x.doubleValue() >= min.y().doubleValue() && y.doubleValue() >= min.x().doubleValue();

        if (horizontalIsOk || verticalIsOk) {
            return;
        }

        throw WiwaException.ORDER_ITEM_PART_INVALID.exception("Invalid dimensions [{0},{1}] minimum is {2}", x, y, min);
    }

    private ManufactureDimensionsData getMax(final long boardId, final Map<Long, OrderBoardData> boards) {
        return new ManufactureDimensionsData(boards.get(boardId).length(), boards.get(boardId).width());
    }

    private ManufactureDimensionsData getCornerDimensions(final PartCornerData partCorner) {
        return switch (partCorner) {
            case final PartCornerStraightData cornerStraight ->
                    new ManufactureDimensionsData(cornerStraight.dimensionX(), cornerStraight.dimensionY());
            case final PartCornerRoundedData cornerRounded ->
                    new ManufactureDimensionsData(cornerRounded.radius(), cornerRounded.radius());
            default ->
                    throw new InvalidParameterException("Unsupported part corner type: " + partCorner.getClass().getSimpleName());
        };
    }

    private void checkCorners(final BigDecimal x, final BigDecimal y, final Map<CornerPosition, ManufactureDimensionsData> cornerDimensions) {
        if (cornerDimensions.isEmpty()) {
            return;
        }

        // TODO
    }

    private void checkEdges(final BigDecimal thickness,
                            final Set<Long> edgeIds,
                            final Map<Long, OrderEdgeData> edges,
                            final ManufacturePropertiesData manufactureProperties) {
        for (final Long edgeId : edgeIds) {
            if (thickness.add(manufactureProperties.edgeWidthAppend()).doubleValue() > edges.get(edgeId).width().doubleValue()) {
                throw WiwaException.ORDER_ITEM_PART_INVALID.exception("Invalid edge width [{0}] minimum is {1}",
                        edges.get(edgeId).width(),
                        thickness.add(manufactureProperties.edgeWidthAppend()));
            }
        }
    }
}
