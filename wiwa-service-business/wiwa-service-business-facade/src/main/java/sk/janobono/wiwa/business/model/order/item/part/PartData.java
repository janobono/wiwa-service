package sk.janobono.wiwa.business.model.order.item.part;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        defaultImpl = PartBasicData.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PartBasicData.class, name = "BASIC"),
        @JsonSubTypes.Type(value = PartFrameData.class, name = "FRAME"),
        @JsonSubTypes.Type(value = PartDuplicatedBasicData.class, name = "DUPLICATED_BASIC"),
        @JsonSubTypes.Type(value = PartDuplicatedFrameData.class, name = "DUPLICATED_FRAME")
})
public interface PartData {
    PartSummaryData summary();
}
