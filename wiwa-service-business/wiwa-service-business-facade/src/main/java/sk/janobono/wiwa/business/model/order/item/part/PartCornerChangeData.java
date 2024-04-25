package sk.janobono.wiwa.business.model.order.item.part;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        defaultImpl = PartCornerChangeStraightData.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PartCornerChangeStraightData.class, name = "STRAIGHT"),
        @JsonSubTypes.Type(value = PartCornerChangeRoundedData.class, name = "ROUNDED")
})
public interface PartCornerChangeData {
}
