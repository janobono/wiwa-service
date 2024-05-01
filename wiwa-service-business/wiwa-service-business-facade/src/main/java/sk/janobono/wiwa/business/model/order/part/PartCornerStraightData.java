package sk.janobono.wiwa.business.model.order.part;

import sk.janobono.wiwa.business.model.DimensionsData;

public record PartCornerStraightData(Long edgeId, DimensionsData dimensions) implements PartCornerData {
}
