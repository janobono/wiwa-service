package sk.janobono.wiwa.api.model.order.item.part;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        defaultImpl = PartCornerChangeStraightWebDto.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PartCornerChangeStraightWebDto.class, name = "STRAIGHT"),
        @JsonSubTypes.Type(value = PartCornerChangeRoundedWebDto.class, name = "ROUNDED")
})
public interface PartCornerChangeWebDto {
}
