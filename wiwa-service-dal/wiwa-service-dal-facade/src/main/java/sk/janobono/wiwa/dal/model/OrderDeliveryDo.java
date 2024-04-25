package sk.janobono.wiwa.dal.model;

import sk.janobono.wiwa.model.OrderPackageType;

import java.time.LocalDate;

public record OrderDeliveryDo(
        LocalDate delivery,
        OrderPackageType packageType
) {
}
