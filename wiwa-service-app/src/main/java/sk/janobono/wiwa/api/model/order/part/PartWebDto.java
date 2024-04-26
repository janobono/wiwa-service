package sk.janobono.wiwa.api.model.order.part;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        defaultImpl = PartBasicWebDto.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PartBasicWebDto.class, name = "BASIC"),
        @JsonSubTypes.Type(value = PartFrameWebDto.class, name = "FRAME"),
        @JsonSubTypes.Type(value = PartDuplicatedBasicWebDto.class, name = "DUPLICATED_BASIC"),
        @JsonSubTypes.Type(value = PartDuplicatedFrameWebDto.class, name = "DUPLICATED_FRAME")
})
public interface PartWebDto {
}
