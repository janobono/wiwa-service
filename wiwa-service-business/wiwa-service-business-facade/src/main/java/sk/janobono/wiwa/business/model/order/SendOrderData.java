package sk.janobono.wiwa.business.model.order;

import java.time.LocalDate;

public record SendOrderData(
        OrderContactData contact,
        boolean gdprAgreement,
        boolean businessConditionsAgreement,
        LocalDate deliveryDate
) {
}
