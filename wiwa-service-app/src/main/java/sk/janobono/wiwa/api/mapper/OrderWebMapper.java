package sk.janobono.wiwa.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.api.model.order.*;
import sk.janobono.wiwa.api.model.order.part.*;
import sk.janobono.wiwa.api.model.order.summary.OrderItemSummaryWebDto;
import sk.janobono.wiwa.api.model.order.summary.OrderSummaryWebDto;
import sk.janobono.wiwa.business.model.order.*;
import sk.janobono.wiwa.business.model.order.part.*;
import sk.janobono.wiwa.business.model.order.summary.OrderItemSummaryData;
import sk.janobono.wiwa.business.model.order.summary.OrderSummaryData;

import java.security.InvalidParameterException;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface OrderWebMapper {

    OrderBoardWebDto mapToWebDto(OrderBoardData orderBoardData);

    OrderCommentWebDto mapToWebDto(OrderCommentData orderComment);

    OrderContactWebDto mapToWebDto(OrderContactData orderContact);

    OrderEdgeWebDto mapToWebDto(OrderEdgeData orderEdgeData);

    OrderUserWebDto mapToWebDto(OrderUserData orderUser);

    OrderWebDto mapToWebDto(OrderData order);

    OrderItemWebDto mapToWebDto(OrderItemData orderItem);

    default PartWebDto mapToWebDto(final PartData part) {
        if (part != null) {
            return switch (part) {
                case final PartBasicData partBasicData -> mapToWebDto(partBasicData);
                case final PartFrameData partFrameData -> mapToWebDto(partFrameData);
                case final PartDuplicatedBasicData partDuplicatedBasicData -> mapToWebDto(partDuplicatedBasicData);
                case final PartDuplicatedFrameData partDuplicatedFrameData -> mapToWebDto(partDuplicatedFrameData);
                default ->
                        throw new InvalidParameterException("Unsupported part type: " + part.getClass().getSimpleName());
            };
        }
        return null;
    }

    PartBasicWebDto mapToWebDto(PartBasicData part);

    PartFrameWebDto mapToWebDto(PartFrameData part);

    PartDuplicatedBasicWebDto mapToWebDto(PartDuplicatedBasicData part);

    PartDuplicatedFrameWebDto mapToWebDto(PartDuplicatedFrameData part);

    default PartCornerWebDto mapToWebDto(final PartCornerData partCorner) {
        if (partCorner != null) {
            return switch (partCorner) {
                case final PartCornerStraightData partCornerStraightData -> mapToWebDto(partCornerStraightData);
                case final PartCornerRoundedData partCornerRoundedData -> mapToWebDto(partCornerRoundedData);
                default ->
                        throw new InvalidParameterException("Unsupported part corner type: " + partCorner.getClass().getSimpleName());
            };
        }
        return null;
    }

    PartCornerStraightWebDto mapToWebDto(PartCornerStraightData partCorner);

    PartCornerRoundedWebDto mapToWebDto(PartCornerRoundedData partCorner);

    OrderItemSummaryWebDto mapToWebDto(OrderItemSummaryData orderItemSummary);

    OrderSummaryWebDto mapToWebDto(OrderSummaryData orderSummary);

    OrderCommentChangeData mapToData(OrderCommentChangeWebDto orderCommentChange);

    SendOrderData mapToData(SendOrderWebDto sendOrder);

    OrderContactData mapToData(OrderContactWebDto orderContact);

    OrderStatusChangeData mapToData(OrderStatusChangeWebDto orderStatusChange);

    OrderItemChangeData mapToData(OrderItemChangeWebDto orderItemChange);

    default PartData mapToData(final PartWebDto part) {
        if (part != null) {
            return switch (part) {
                case final PartBasicWebDto partBasicWebDto -> mapToData(partBasicWebDto);
                case final PartFrameWebDto partFrameWebDto -> mapToData(partFrameWebDto);
                case final PartDuplicatedBasicWebDto partDuplicatedBasicWebDto -> mapToData(partDuplicatedBasicWebDto);
                case final PartDuplicatedFrameWebDto partDuplicatedFrameWebDto -> mapToData(partDuplicatedFrameWebDto);
                default ->
                        throw new InvalidParameterException("Unsupported part type: " + part.getClass().getSimpleName());
            };
        }
        return null;
    }

    PartBasicData mapToData(PartBasicWebDto part);

    PartFrameData mapToData(PartFrameWebDto part);

    PartDuplicatedBasicData mapToData(PartDuplicatedBasicWebDto part);

    PartDuplicatedFrameData mapToData(PartDuplicatedFrameWebDto part);

    default PartCornerData mapToData(final PartCornerWebDto partCorner) {
        if (partCorner != null) {
            return switch (partCorner) {
                case final PartCornerStraightWebDto partCornerStraightWebDto -> mapToData(partCornerStraightWebDto);
                case final PartCornerRoundedWebDto partCornerRoundedWebDto -> mapToData(partCornerRoundedWebDto);
                default ->
                        throw new InvalidParameterException("Unsupported part corner type: " + partCorner.getClass().getSimpleName());
            };
        }
        return null;
    }

    PartCornerStraightData mapToData(PartCornerStraightWebDto partCorner);

    PartCornerRoundedData mapToData(PartCornerRoundedWebDto partCorner);
}
