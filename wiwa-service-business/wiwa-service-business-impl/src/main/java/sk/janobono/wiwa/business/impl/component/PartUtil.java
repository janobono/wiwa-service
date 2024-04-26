package sk.janobono.wiwa.business.impl.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sk.janobono.wiwa.business.model.application.ManufacturePropertiesData;
import sk.janobono.wiwa.business.model.order.OrderBoardData;
import sk.janobono.wiwa.business.model.order.OrderEdgeData;
import sk.janobono.wiwa.business.model.order.part.*;
import sk.janobono.wiwa.exception.WiwaException;

import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

    private void validateBasic(final PartBasicData partBasic,
                               final Map<Long, OrderBoardData> boards,
                               final Map<Long, OrderEdgeData> edges,
                               final ManufacturePropertiesData manufactureProperties) {
        // TODO
    }

    private void validateFrame(final PartFrameData partFrame,
                               final Map<Long, OrderBoardData> boards,
                               final Map<Long, OrderEdgeData> edges,
                               final ManufacturePropertiesData manufactureProperties) {
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

    private void validate(final PartCornerData partCorner, final ManufacturePropertiesData manufactureProperties) {
        switch (partCorner) {
            case final PartCornerStraightData cornerStraight ->
                    validateCornerStraight(cornerStraight, manufactureProperties);
            case final PartCornerRoundedData cornerRounded ->
                    validateCornerRounded(cornerRounded, manufactureProperties);
            default ->
                    throw new InvalidParameterException("Unsupported part corner type: " + partCorner.getClass().getSimpleName());
        }
    }

    private void validateCornerStraight(final PartCornerStraightData cornerStraight, final ManufacturePropertiesData manufactureProperties) {
        if (manufactureProperties.minimalCornerStraightDimension().doubleValue() > cornerStraight.dimensionX().doubleValue() ||
                manufactureProperties.minimalCornerStraightDimension().doubleValue() > cornerStraight.dimensionY().doubleValue()) {
            throw WiwaException.ORDER_ITEM_PART_CORNER_INVALID.exception("Corner X or Y less than minimum");
        }
    }

    private void validateCornerRounded(final PartCornerRoundedData cornerRounded, final ManufacturePropertiesData manufactureProperties) {
        if (manufactureProperties.minimalCornerStraightDimension().doubleValue() > cornerRounded.radius().doubleValue()) {
            throw WiwaException.ORDER_ITEM_PART_CORNER_INVALID.exception("Corner radius less than minimum");
        }
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
}
