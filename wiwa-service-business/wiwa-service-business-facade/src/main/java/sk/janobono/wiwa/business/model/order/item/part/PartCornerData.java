package sk.janobono.wiwa.business.model.order.item.part;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        defaultImpl = PartCornerStraightData.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PartCornerStraightData.class, name = "STRAIGHT"),
        @JsonSubTypes.Type(value = PartCornerRoundedData.class, name = "ROUNDED")
})
public interface PartCornerData {
}
