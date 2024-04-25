package sk.janobono.wiwa.business.impl.model.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import sk.janobono.wiwa.business.model.order.part.PartData;
import sk.janobono.wiwa.dal.model.OderItemSummaryDo;

@Builder
@Getter
@Setter
public class OrderItemJson {
    public PartData getParData() {
        return PartData.builder()
                // TODO

                .build();
    }

    public OderItemSummaryDo getOderItemSummary() {
        // TODO
        return OderItemSummaryDo.builder()



                .build();
    }
}
