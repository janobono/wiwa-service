package sk.janobono.wiwa.api.model.order.item.part;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        defaultImpl = PartChangeBasicWebDto.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PartChangeBasicWebDto.class, name = "BASIC"),
        @JsonSubTypes.Type(value = PartChangeFrameWebDto.class, name = "FRAME"),
        @JsonSubTypes.Type(value = PartChangeDuplicatedBasicWebDto.class, name = "DUPLICATED_BASIC"),
        @JsonSubTypes.Type(value = PartChangeDuplicatedFrameWebDto.class, name = "DUPLICATED_FRAME")
})
public interface PartChangeWebDto {
}
