package sk.janobono.wiwa.business.model.order.part;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.model.BoardPosition;
import sk.janobono.wiwa.model.CornerPosition;
import sk.janobono.wiwa.model.EdgePosition;

import java.util.Map;

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
    Map<BoardPosition, DimensionsData> dimensions();

    Map<BoardPosition, Long> boards();

    Map<EdgePosition, Long> edges();

    Map<CornerPosition, PartCornerData> corners();
}
