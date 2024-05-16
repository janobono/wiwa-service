package sk.janobono.wiwa.business.model.order.part;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import sk.janobono.wiwa.business.model.DimensionsData;
import sk.janobono.wiwa.model.BoardPosition;
import sk.janobono.wiwa.model.CornerPosition;
import sk.janobono.wiwa.model.EdgePosition;
import sk.janobono.wiwa.model.PartType;

import java.util.Map;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type",
        defaultImpl = PartBasicData.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PartBasicData.class, name = PartType.BASIC),
        @JsonSubTypes.Type(value = PartFrameData.class, name = PartType.FRAME),
        @JsonSubTypes.Type(value = PartDuplicatedBasicData.class, name = PartType.DUPLICATED_BASIC),
        @JsonSubTypes.Type(value = PartDuplicatedFrameData.class, name = PartType.DUPLICATED_FRAME)
})
public interface PartData {
    Map<BoardPosition, DimensionsData> dimensions();

    Map<BoardPosition, Long> boards();

    Map<EdgePosition, Long> edges();

    Map<CornerPosition, PartCornerData> corners();
}
