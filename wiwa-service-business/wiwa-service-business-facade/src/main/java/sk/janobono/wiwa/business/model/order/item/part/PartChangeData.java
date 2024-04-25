package sk.janobono.wiwa.business.model.order.item.part;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        defaultImpl = PartChangeBasicData.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PartChangeBasicData.class, name = "BASIC"),
        @JsonSubTypes.Type(value = PartChangeFrameData.class, name = "FRAME"),
        @JsonSubTypes.Type(value = PartChangeDuplicatedBasicData.class, name = "DUPLICATED_BASIC"),
        @JsonSubTypes.Type(value = PartChangeDuplicatedFrameData.class, name = "DUPLICATED_FRAME")
})
public interface PartChangeData {
}
