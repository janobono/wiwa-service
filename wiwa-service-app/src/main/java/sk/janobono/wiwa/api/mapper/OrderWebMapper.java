package sk.janobono.wiwa.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import sk.janobono.wiwa.api.model.order.*;
import sk.janobono.wiwa.api.model.order.item.OrderItemChangeWebDto;
import sk.janobono.wiwa.api.model.order.item.OrderItemWebDto;
import sk.janobono.wiwa.api.model.order.item.part.*;
import sk.janobono.wiwa.business.model.order.*;
import sk.janobono.wiwa.business.model.order.item.OrderItemChangeData;
import sk.janobono.wiwa.business.model.order.item.OrderItemData;
import sk.janobono.wiwa.business.model.order.item.part.*;

import java.security.InvalidParameterException;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface OrderWebMapper {

    OrderBoardWebDto mapToWebDto(OrderBoardData orderBoardData);

    OrderCommentWebDto mapToWebDto(OrderCommentData orderComment);

    OrderContactWebDto mapToWebDto(OrderContactData orderContact);

    OrderEdgeWebDto mapToWebDto(OrderEdgeData orderEdgeData);

    OrderSummaryWebDto mapToWebDto(OrderSummaryData orderSummary);

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

    OrderCommentChangeData mapToData(OrderCommentChangeWebDto orderCommentChange);

    SendOrderData mapToData(SendOrderWebDto sendOrder);

    OrderContactData mapToData(OrderContactWebDto orderContact);

    OrderStatusChangeData mapToData(OrderStatusChangeWebDto orderStatusChange);

    OrderItemChangeData mapToData(OrderItemChangeWebDto orderItemChange);

    default PartChangeData mapToData(final PartChangeWebDto partChange) {
        if (partChange != null) {
            return switch (partChange) {
                case final PartChangeBasicWebDto partChangeBasicWebDto -> mapToData(partChangeBasicWebDto);
                case final PartChangeFrameWebDto partChangeFrameWebDto -> mapToData(partChangeFrameWebDto);
                case final PartChangeDuplicatedBasicWebDto partChangeDuplicatedBasicWebDto ->
                        mapToData(partChangeDuplicatedBasicWebDto);
                case final PartChangeDuplicatedFrameWebDto partChangeDuplicatedFrameWebDto ->
                        mapToData(partChangeDuplicatedFrameWebDto);
                default ->
                        throw new InvalidParameterException("Unsupported part change type: " + partChange.getClass().getSimpleName());
            };
        }
        return null;
    }

    PartChangeBasicData mapToData(PartChangeBasicWebDto partChange);

    PartChangeFrameData mapToData(PartChangeFrameWebDto partChange);

    PartChangeDuplicatedBasicData mapToData(PartChangeDuplicatedBasicWebDto partChange);

    PartChangeDuplicatedFrameData mapToData(PartChangeDuplicatedFrameWebDto partChange);

    default PartCornerChangeData mapToData(final PartCornerChangeWebDto partCornerChange) {
        if (partCornerChange != null) {
            return switch (partCornerChange) {
                case final PartCornerChangeStraightWebDto partCornerChangeStraightWebDto ->
                        mapToData(partCornerChangeStraightWebDto);
                case final PartCornerChangeRoundedWebDto partCornerChangeRoundedWebDto ->
                        mapToData(partCornerChangeRoundedWebDto);
                default ->
                        throw new InvalidParameterException("Unsupported part corner change type: " + partCornerChange.getClass().getSimpleName());
            };
        }
        return null;
    }

    PartCornerChangeStraightData mapToData(PartCornerChangeStraightWebDto partChange);

    PartCornerChangeRoundedData mapToData(PartCornerChangeRoundedWebDto partChange);
}
