package sk.janobono.wiwa.business.model.order;

import sk.janobono.wiwa.model.OrderPackageType;

import java.time.LocalDate;

public record SendOrderData(
        OrderContactData contact,
        boolean gdprAgreement,
        boolean businessConditionsAgreement,
        LocalDate deliveryDate,
        OrderPackageType packageType
) {
}
