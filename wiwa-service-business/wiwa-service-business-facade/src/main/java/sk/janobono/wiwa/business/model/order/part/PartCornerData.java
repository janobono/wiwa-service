package sk.janobono.wiwa.business.model.order.part;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.model.PartCornerType;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        defaultImpl = PartCornerStraightData.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PartCornerStraightData.class, name = PartCornerType.STRAIGHT),
        @JsonSubTypes.Type(value = PartCornerRoundedData.class, name = PartCornerType.ROUNDED)
})
public interface PartCornerData {
    Long edgeId();

    DimensionsData dimensions();
}
