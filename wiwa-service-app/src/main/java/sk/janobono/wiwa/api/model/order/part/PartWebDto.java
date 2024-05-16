package sk.janobono.wiwa.api.model.order.part;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import sk.janobono.wiwa.model.PartType;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        defaultImpl = PartBasicWebDto.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PartBasicWebDto.class, name = PartType.BASIC),
        @JsonSubTypes.Type(value = PartFrameWebDto.class, name = PartType.FRAME),
        @JsonSubTypes.Type(value = PartDuplicatedBasicWebDto.class, name = PartType.DUPLICATED_BASIC),
        @JsonSubTypes.Type(value = PartDuplicatedFrameWebDto.class, name = PartType.DUPLICATED_FRAME)
})
public interface PartWebDto {
}
