package sk.janobono.wiwa.api.model.order.item.part;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        defaultImpl = PartCornerStraightWebDto.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PartCornerStraightWebDto.class, name = "STRAIGHT"),
        @JsonSubTypes.Type(value = PartCornerRoundedWebDto.class, name = "ROUNDED")
})
public interface PartCornerWebDto {
}
