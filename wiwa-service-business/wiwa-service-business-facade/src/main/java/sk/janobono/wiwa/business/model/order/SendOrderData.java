package sk.janobono.wiwa.business.model.order;

import lombok.Builder;
import sk.janobono.wiwa.model.OrderPackageType;

import java.time.LocalDate;

@Builder
public record SendOrderData(
        OrderContactData contact,
        boolean gdprAgreement,
        boolean businessConditionsAgreement,
        LocalDate deliveryDate,
        OrderPackageType packageType
) {
}
