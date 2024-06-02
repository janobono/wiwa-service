package sk.janobono.wiwa.api.model.order.part;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import sk.janobono.wiwa.model.PartCornerType;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        defaultImpl = PartCornerStraightWebDto.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PartCornerStraightWebDto.class, name = PartCornerType.STRAIGHT),
        @JsonSubTypes.Type(value = PartCornerRoundedWebDto.class, name = PartCornerType.ROUNDED)
})
public interface PartCornerWebDto {
}
